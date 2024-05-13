package com.example.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AgentConfig.class);

    @Autowired
    JobRepository jobRepository;
    @Autowired
    PlatformTransactionManager transactionManager;

    @Bean
    public Tasklet tasklet1() {
        return (contribution, chunkContext) -> {
            log.info("Executing Agent 1 Job....");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public JobListener jobListener() {
        return new JobListener();
    }

    public Step agent1Step() {
        return new StepBuilder("agent1Step", jobRepository)
                .tasklet(tasklet1(), transactionManager)
                .build();
    }

    @Bean
    public Job job1() {
        return new JobBuilder("ID1", jobRepository)
                .start(agent1Step())
                .listener(jobListener())
                .build();
    }
    @Bean
    public Tasklet tasklet2() {
        return (contribution, chunkContext) -> {
            log.info("Executing Agent 2 Job....");
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
        return new JobBuilder("ID2", jobRepository)
                .start(agent2Step())
                .listener(jobListener())
                .build();
    }


}
