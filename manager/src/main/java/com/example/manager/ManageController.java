package com.example.manager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManageController {
    private final AgentTracker agentTracker;

    public ManageController(AgentTracker agentTracker) {
        this.agentTracker = agentTracker;
    }

    @GetMapping("/start/agent/{id}")
    public String startAgent(@PathVariable String id) {
        return agentTracker.startAgent(id);
    }

    @GetMapping("/stop/agent/{id}")
    public void stopJob(@PathVariable String id) {
        agentTracker.stopJob(id);
    }


}
