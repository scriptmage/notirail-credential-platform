package com.notirail.credential.cache.service.service;

import com.google.protobuf.Timestamp;
import com.notirail.credential.cache.service.domain.cache.CredentialCacheEntry;
import com.notirail.credential.cache.service.domain.event.CredentialEventData;
import com.notirail.credential.cache.service.domain.event.CredentialEventType;
import com.notirail.credential.cache.service.proto.ApplyResult;
import com.notirail.credential.cache.service.proto.CredentialEvent;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CredentialEventMapper {

    public CredentialEventData toDomain(final CredentialEvent source) {
        final CredentialCacheEntry snapshot = source.hasSnapshot() ? toSnapshot(source) : null;
        return new CredentialEventData(
            source.getEventId(),
            toDomainEventType(source),
            source.getCredentialId(),
            source.getKeyId(),
            snapshot,
            toInstant(source.getOccurredAt())
        );
    }

    public ApplyResult toProto(final CredentialEventService.ApplyResult result) {
        return switch (result) {
            case APPLIED -> ApplyResult.APPLIED;
            case ALREADY_APPLIED -> ApplyResult.ALREADY_APPLIED;
        };
    }

    private CredentialCacheEntry toSnapshot(final CredentialEvent source) {
        final var snapshot = source.getSnapshot();
        return new CredentialCacheEntry(
            snapshot.getCredentialId(),
            snapshot.getKeyId(),
            snapshot.getSecretHash(),
            snapshot.getPartnerId(),
            snapshot.hasProjectId() ? snapshot.getProjectId() : null,
            List.copyOf(snapshot.getScopesList()),
            snapshot.hasExpiresAt() ? toInstant(snapshot.getExpiresAt()) : null
        );
    }

    private CredentialEventType toDomainEventType(final CredentialEvent source) {
        return switch (source.getEventType()) {
            case CREDENTIAL_CREATED -> CredentialEventType.CREDENTIAL_CREATED;
            case CREDENTIAL_REVOKED -> CredentialEventType.CREDENTIAL_REVOKED;
            case CREDENTIAL_UPDATED -> CredentialEventType.CREDENTIAL_UPDATED;
            case UNRECOGNIZED,
                 CREDENTIAL_UNSPECIFIED -> throw new IllegalArgumentException("Unsupported credential event type");
        };
    }

    private Instant toInstant(final Timestamp timestamp) {
        return Instant.ofEpochSecond(
            timestamp.getSeconds(),
            timestamp.getNanos()
        );
    }

}
