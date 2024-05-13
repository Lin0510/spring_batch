package com.example.manager;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class ManageController {
    private static final Logger log = LoggerFactory.getLogger(ManageController.class);
    private Process agentProcess;


    @GetMapping("/start/agent/{jobName}")
    public String startAgent(@PathVariable String jobName) {
        log.debug("----------------startAgent----------------");
        String cmd = "java -jar D:/Lin/Documents/Studio/project/batch/agent/target/agent.jar --spring.batch.job.name=" + jobName;
        if (ObjectUtils.isNotEmpty(agentProcess)) {
            destroyProcess();
        }
        try {
            agentProcess = Runtime.getRuntime().exec(cmd);

            ExecutorService executor = Executors.newFixedThreadPool(2);

            executor.submit(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(agentProcess.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        log.info(line);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executor.submit(() -> {
                try (BufferedReader err = new BufferedReader(new InputStreamReader(agentProcess.getErrorStream()))) {
                    String line;
                    while ((line = err.readLine()) != null) {
                        log.error("err: {}", line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            int exitCode = agentProcess.waitFor();
            log.debug("AgentProcess exited with code: {}", exitCode);

            executor.shutdown();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return "啟動 agent JobName = " + jobName;
    }

    public void destroyProcess() {
        ProcessHandle processHandle = agentProcess.toHandle();
        processHandle.destroyForcibly();
        printProcessInfo(processHandle);
    }

    private void printProcessInfo(ProcessHandle processHandle) {
        log.debug("---------");
        log.debug("Id: {}", processHandle.pid());
        log.debug("isAlive(): {}", processHandle.isAlive());
        log.debug("isSupportsNormalTermination(): {}", processHandle.supportsNormalTermination());

        ProcessHandle.Info processInfo = processHandle.info();
        log.debug("Info: {}", processInfo);
        log.debug("Info arguments().isPresent(): {}", processInfo.arguments().isPresent());
        log.debug("Info command().isPresent(): {}", processInfo.command().isPresent());
        log.debug("Info totalCpuDuration().isPresent(): {}", processInfo.totalCpuDuration().isPresent());
        log.debug("Info user().isPresent(): {}", processInfo.user().isPresent());
    }

}
