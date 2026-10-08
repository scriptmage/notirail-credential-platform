package com.notirail.credential.cache.service.domain.event;

import com.notirail.credential.cache.service.domain.cache.CredentialCacheEntry;
import java.time.Instant;

public record CredentialEventData(
    String eventId,
    CredentialEventType eventType,
    String credentialId,
    String keyId,
    CredentialCacheEntry snapshot,
    Instant occurredAt
) {

}
