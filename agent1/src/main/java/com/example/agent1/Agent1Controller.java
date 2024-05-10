package com.example.agent1;

import jakarta.annotation.PreDestroy;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/agent1")
public class Agent1Controller {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job agent1Job;
    @Autowired
    private Agent1Properties properties;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @GetMapping("/job")
    public String hello() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> System.out.println("agent1 shutdown..."))
        );

        String paramId = properties.getId();
        executorService.submit(() -> {
            try {
                jobLauncher.run(agent1Job, new JobParameters());
                System.out.println("Received ID: " + paramId);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("Job completed for ID: " + paramId);
                System.exit(0);
            }
        });

        return "Processing ID: " + paramId;
    }

}
