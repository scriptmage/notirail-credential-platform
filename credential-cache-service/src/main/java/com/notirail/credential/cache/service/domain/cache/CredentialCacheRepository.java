package com.notirail.credential.cache.service.domain.cache;

import java.time.Duration;
import java.util.Optional;

public interface CredentialCacheRepository {

    void saveActive(CredentialCacheEntry entry);

    void revoke(String keyId, Duration markerTtl);

    Optional<CredentialCacheEntry> findByKeyId(String keyId);

    boolean isRevoked(String keyId);

    boolean hasProcessedEvent(String eventId);

    void markEventProcessed(String eventId, Duration ttl);

}
