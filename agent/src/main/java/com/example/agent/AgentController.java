package com.example.agent;

import org.springframework.batch.core.launch.JobOperator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final JobOperator jobOperator;

    public AgentController(JobOperator jobOperator) {
        this.jobOperator = jobOperator;
    }

    @GetMapping("/stop/{jobName}")
    public void stopJob(@PathVariable String jobName) throws Exception {
        Set<Long> executionIds = jobOperator.getRunningExecutions(jobName);
        for (Long executionId : executionIds) {
            jobOperator.stop(executionId);
        }
    }

}
