package com.example.agent1;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class Agent1Config {

    @Autowired
    JobRepository jobRepository;
    @Autowired
    PlatformTransactionManager transactionManager;

    @Bean
    public Tasklet tasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("Executing Agent 1 Job....");
            return RepeatStatus.FINISHED;
        };
    }

    public Step agent1Step() {
        return new StepBuilder("agent1Step", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .build();
    }

    @Bean
    public Job agent1Job() {
        return new JobBuilder("agent1Job", jobRepository)
                .start(agent1Step())
                .build();
    }
}
