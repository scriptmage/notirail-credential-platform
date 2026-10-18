package com.notirail.credential.relay.service.service;

import com.notirail.credential.relay.service.config.RelayConfiguration.RelayConfigurationProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RelayScheduler {

    private final TaskScheduler outboxRelayTaskScheduler;

    private final OutboxRelayService outboxRelayService;

    private final RelayConfigurationProperties config;

    @PostConstruct
    public void start() {
        outboxRelayTaskScheduler.scheduleAtFixedRate(
            outboxRelayService::pollAndDeliver,
            config.getPollInterval()
        );

        log.info("Credential outbox relay started with poll interval {} seconds", config.getPollInterval().toSeconds());
    }

}
