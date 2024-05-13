package com.example.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AgentTracker {
    private static final Logger log = LoggerFactory.getLogger(AgentTracker.class);
    private Map<String, Process> runningProcesses = new ConcurrentHashMap<>();
    private Map<String, String> runningJobPort = new ConcurrentHashMap<>();
    private AtomicInteger port = new AtomicInteger(8081);
    @Value("${agent.jar-path}")
    private String agentJarPath;

    public String startAgent(String id) {
        Runtime.getRuntime().addShutdownHook(
                new Thread(this::cleanup)
        );
        log.debug("----------------startAgent----------------");
        String currentPort;
        try {
            if (Objects.isNull(runningJobPort.get(id))) {
                currentPort = String.valueOf(port.getAndIncrement());
            } else {
                currentPort = runningJobPort.get(id);
            }
            log.debug("port: {}", currentPort);
            Process process = new ProcessBuilder()
                    .command("java", "-jar", agentJarPath,
                            "--spring.batch.job.name=" + id, "--server.port=" + currentPort)
                    .start();
            long pid = process.pid();
            runningProcesses.put(id + "-" + currentPort, process);
            runningJobPort.put(id, currentPort);

            monitorProcessOutput(process);

            return "JVM pid: " + pid;
        } catch (IOException e) {
            log.error("start process fail: {}", e.getMessage());
            return "start fail";
        }
    }

    private void monitorProcessOutput(Process process) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    log.info(line);
                }
            } catch (IOException e) {
                log.error("Read process output exception: {}", e.getMessage());
            }
        });

        executor.submit(() -> {
            try (BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = err.readLine()) != null) {
                    log.error("process output exception: {}", line);
                }
            } catch (IOException e) {
                log.error("Read process output exception: {}", e.getMessage());
            }
        });

        executor.shutdown();
    }

    public void cleanup() {
        log.debug("------------clean up------------");
        for (Map.Entry<String, Process> entry : runningProcesses.entrySet()) {
            Process process = entry.getValue();
            if (process.isAlive()) {
                process.destroyForcibly();
                log.info("destroy process: {}", entry.getKey());
            }
        }
    }

    public void stopJob(String id) {
        String port = runningJobPort.get(id);
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:%s/api/agent/stop/%s".formatted(port, id);
        log.debug("port: {}, jobName: {}", port, id);
        restTemplate.exchange(
                String.format(url, port, id),
                HttpMethod.GET,
                null,
                Void.class
        );
    }


}
