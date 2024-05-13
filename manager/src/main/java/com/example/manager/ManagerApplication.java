package com.example.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@SpringBootApplication
public class ManagerApplication {

    private static final Logger log = LoggerFactory.getLogger(ManagerApplication.class);

    private final Environment env;

    public ManagerApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ManagerApplication.class);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String applicationName = env.getProperty("spring.application.name");
        String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
        String serverPort = env.getProperty("server.port") == null ? "8080" : env.getProperty("server.port");
        String contextPath = Optional.ofNullable(env.getProperty("server.servlet.context-path")).filter(StringUtils::isNotBlank).orElse("/");
        String baseUrl = protocol + "://localhost:" + serverPort + contextPath;
        log.info("""
                                
                ----------------------------------------------------------
                	Application '{}' is running! Access URLs:
                	Local:         {}
                ----------------------------------------------------------
                """, applicationName, baseUrl);
    }

}
