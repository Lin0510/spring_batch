package com.example.agent1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Agent1Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Agent1Application.class, args);
        Agent1Properties properties = context.getBean(Agent1Properties.class);
        if (args.length > 0) {
            properties.setId(args[0]);
        }
    }

}
