package com.marlisa.workout_app_backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotenvConfig {
    private final ConfigurableEnvironment environment;

    public DotenvConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.load();
        Map<String, Object> envMap = new HashMap<>();

        dotenv.entries().forEach(entry -> envMap.put(entry.getKey(), entry.getValue()));

        MapPropertySource propertySource = new MapPropertySource("dotenvProperties", envMap);
        environment.getPropertySources().addLast(propertySource);
    }
}
