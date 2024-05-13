package com.example.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;

public class JobListener {
    private static final Logger log = LoggerFactory.getLogger(JobListener.class);

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        log.info("執行後-status：{}", jobExecution.getStatus());
        System.exit(0);
    }
}
