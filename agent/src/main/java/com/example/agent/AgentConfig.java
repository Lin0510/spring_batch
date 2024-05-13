package com.example.agent;

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
public class AgentConfig {

    @Autowired
    JobRepository jobRepository;
    @Autowired
    PlatformTransactionManager transactionManager;

    @Bean
    public Tasklet tasklet1() {
        return (contribution, chunkContext) -> {
            System.out.println("Executing Agent 1 Job....");
            return RepeatStatus.FINISHED;
        };
    }

    public Step agent1Step() {
        return new StepBuilder("agent1Step", jobRepository)
                .tasklet(tasklet1(), transactionManager)
                .build();
    }

    @Bean
    public Job job1() {
        return new JobBuilder("job1", jobRepository)
                .start(agent1Step())
                .build();
    }
    @Bean
    public Tasklet tasklet2() {
        return (contribution, chunkContext) -> {
            System.out.println("Executing Agent 2 Job....");
            return RepeatStatus.CONTINUABLE;
        };
    }

    @Bean
    public Step agent2Step() {
        return new StepBuilder("agent2Step", jobRepository)
                .tasklet(tasklet2(), transactionManager)
                .build();
    }

    @Bean
    public Job job2() {
        return new JobBuilder("job2", jobRepository)
                .start(agent2Step())
                .build();
    }


}
