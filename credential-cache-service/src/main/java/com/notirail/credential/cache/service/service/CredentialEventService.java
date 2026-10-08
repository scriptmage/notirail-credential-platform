package com.notirail.credential.cache.service.service;

import static java.util.Objects.isNull;

import com.notirail.credential.cache.service.config.RedisConfiguration.RedisConfigurationProperties;
import com.notirail.credential.cache.service.domain.cache.CredentialCacheRepository;
import com.notirail.credential.cache.service.domain.event.CredentialEventData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CredentialEventService {

    private final CredentialCacheRepository repository;

    private final RedisConfigurationProperties properties;

    public ApplyResult apply(final CredentialEventData event) {
        if (repository.hasProcessedEvent(event.eventId())) {
            return ApplyResult.ALREADY_APPLIED;
        }

        switch (event.eventType()) {
            case CREDENTIAL_CREATED,
                 CREDENTIAL_UPDATED -> applySnapshot(event);

            case CREDENTIAL_REVOKED -> repository.revoke(
                event.keyId(),
                properties.revokedMarkerTtl()
            );
        }

        repository.markEventProcessed(
            event.eventId(),
            properties.eventIdempotencyTtl()
        );

        return ApplyResult.APPLIED;
    }

    private void applySnapshot(final CredentialEventData event) {
        if (isNull(event.snapshot())) {
            throw new IllegalArgumentException("Snapshot is required for %s".formatted(event.eventType()));
        }
        repository.saveActive(event.snapshot());
    }

    public enum ApplyResult {
        APPLIED,
        ALREADY_APPLIED
    }

}
