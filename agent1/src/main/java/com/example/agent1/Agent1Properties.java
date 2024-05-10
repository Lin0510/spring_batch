package com.example.agent1;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "agent1")
@Getter
@Setter
public class Agent1Properties {
    private String id;
}
