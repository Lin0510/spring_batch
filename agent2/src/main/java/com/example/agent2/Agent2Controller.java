package com.example.agent2;

import jakarta.annotation.PreDestroy;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/agent2")
public class Agent2Controller {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Agent2Properties properties;

    @Autowired
    private Job agent2Job;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @GetMapping("/job")
    public String hello() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> System.out.println("agent2 shutdown..."))
        );
        String id = properties.getId();

        executorService.submit(() -> {
            try {
                jobLauncher.run(agent2Job, new JobParameters());
                System.out.println("Received ID: " + id);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("Job completed for ID: " + id);
                System.exit(0);
            }
        });

        return "Processing ID: " + id;
    }

    @PreDestroy
    public void cleanup() {
        executorService.shutdown();
    }

    @GetMapping("/shutdown")
    public void shutdown() {
        new Thread(() -> System.exit(0)).start();
    }

}
