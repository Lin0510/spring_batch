package com.example.agent2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "agent2")
@Getter
@Setter
public class Agent2Properties {
    private String id;
}
