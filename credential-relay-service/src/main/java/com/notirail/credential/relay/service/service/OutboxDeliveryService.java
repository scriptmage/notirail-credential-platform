package com.notirail.credential.relay.service.service;

import com.notirail.credential.relay.service.config.RelayConfiguration.RelayConfigurationProperties;
import com.notirail.credential.relay.service.domain.credential.CredentialCacheClient;
import com.notirail.credential.relay.service.domain.credential.CredentialCacheClient.DeliveryOutcome;
import com.notirail.credential.relay.service.domain.event.OutboxEventEntity;
import com.notirail.credential.relay.service.domain.event.OutboxEventRepository;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxDeliveryService {

    private final OutboxEventRepository repository;

    private final CredentialCacheClient cacheClient;

    private final RelayConfigurationProperties properties;

    @Transactional
    public List<OutboxEventEntity> claimBatch() {
        final OffsetDateTime claimDeadline = OffsetDateTime.now().minus(properties.getClaimTimeout());
        final List<OutboxEventEntity> events = repository.findClaimableEvents(
            claimDeadline,
            properties.getBatchSize()
        );
        events.forEach(OutboxEventEntity::claim);
        return events;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deliver(final OutboxEventEntity event) {
        try {
            final DeliveryOutcome outcome = cacheClient.apply(event);

            switch (outcome) {
                case DELIVERED -> markDelivered(event);
                case RETRYABLE_FAILURE -> markRetry(event, "Retryable gRPC failure");
                case PERMANENTLY_FAILED -> event.markDead("Non-retryable gRPC failure");
            }

            repository.save(event);
        } catch (final Exception exception) {
            markRetry(event, exception.getMessage());
            repository.save(event);
        }
    }

    private void markDelivered(final OutboxEventEntity event) {
        event.markDelivered();
        log.info(
            "Credential outbox event delivered: eventId={}, type={}, attempt={}",
            event.getEventId(),
            event.getEventType(),
            event.getAttemptCount()
        );
    }

    private void markRetry(final OutboxEventEntity event, final String error) {
        final int nextAttempt = event.getAttemptCount() + 1;

        if (nextAttempt >= properties.getMaxAttempts()) {
            event.markDead(error);
        } else {
            event.markRetry(nextAvailableAt(nextAttempt), error);
        }

        log.warn(
            "Credential outbox event delivery failed: eventId={}, type={}, nextStatus={}, attempt={}",
            event.getEventId(),
            event.getEventType(),
            event.getStatus(),
            nextAttempt
        );
    }

    private OffsetDateTime nextAvailableAt(final int attempt) {
        final long initial = properties.getInitialBackoff().toMillis();
        final long max = properties.getMaxBackoff().toMillis();
        final long exponential = initial * (1L << Math.min(attempt, 12));

        return OffsetDateTime.now()
            .plus(Duration.ofMillis(Math.min(exponential, max)));
    }

}
