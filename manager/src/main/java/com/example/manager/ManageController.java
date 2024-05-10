package com.example.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class ManageController {
    private static final Logger log = LoggerFactory.getLogger(ManageController.class);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm:ss");

    private static Process agent1Process;
    private static Process agent2Process;

    @GetMapping("/start/agent1")
    public String startAgent1() {
        log.debug("----------------startAgent1----------------");
        String formattedTime = LocalDateTime.now().format(formatter);
        String cmd = "java -jar D:/Lin/Documents/Studio/project/batch/agent1/target/agent1.jar " + formattedTime;

        try {
            agent1Process = Runtime.getRuntime().exec(cmd);

            ExecutorService executor = Executors.newFixedThreadPool(2);

            executor.submit(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(agent1Process.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        log.info(line);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executor.submit(() -> {
                try (BufferedReader err = new BufferedReader(new InputStreamReader(agent1Process.getErrorStream()))) {
                    String line;
                    while ((line = err.readLine()) != null) {
                        log.error("err: {}", line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            int exitCode = agent1Process.waitFor();
            log.debug("Agent1Process exited with code: {}", exitCode);

            executor.shutdown();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return "啟動 agent1 \n 訪問：http://localhost:8081/api/agent1/job，打印ID " + formattedTime;
    }

    @GetMapping("/start/agent2")
    public String startAgent2() {
        // 設定 Agent2 的啟動命令及參數
        log.debug("----------------startAgent2----------------");
        String formattedTime = LocalDateTime.now().format(formatter);
        String cmd = "java -jar D:/Lin/Documents/Studio/project/batch/agent2/target/agent2.jar " + formattedTime;

        try {
            agent2Process = Runtime.getRuntime().exec(cmd);

            ExecutorService executor = Executors.newFixedThreadPool(2);

            executor.submit(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(agent2Process.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        log.info(line);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executor.submit(() -> {
                try (BufferedReader err = new BufferedReader(new InputStreamReader(agent2Process.getErrorStream()))) {
                    String line;
                    while ((line = err.readLine()) != null) {
                        log.error("err: {}", line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            int exitCode = agent2Process.waitFor();
            log.debug("Agent2Process exited with code: {}", exitCode);

            executor.shutdown();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return "啟動 agent2 \n 訪問：http://localhost:8082/api/agent2/job，打印ID " + formattedTime;
    }

    @GetMapping("destroy/agent1")
    public void destroyAgent1() {
        ProcessHandle processHandle = agent1Process.toHandle();
        processHandle.destroyForcibly();
        printProcessInfo(processHandle);
    }

    @GetMapping("destroy/agent2")
    public void destroyAgent2() {
        ProcessHandle processHandle = agent2Process.toHandle();
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
