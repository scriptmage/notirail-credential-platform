package com.notirail.credential.relay.service.config;

import java.time.Duration;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class RelayConfiguration {

    @Bean
    public TaskScheduler outboxRelayTaskScheduler() {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("credential-relay-");
        scheduler.initialize();
        return scheduler;
    }

    @Getter
    @ConfigurationProperties(prefix = "relay")
    public static class RelayConfigurationProperties {

        private final Duration pollInterval = Duration.ofSeconds(1);

        private final int batchSize = 100;

        private final Duration claimTimeout = Duration.ofSeconds(30);

        private final int maxAttempts = 10;

        private final Duration initialBackoff = Duration.ofSeconds(1);

        private final Duration maxBackoff = Duration.ofMinutes(5);

    }

}
