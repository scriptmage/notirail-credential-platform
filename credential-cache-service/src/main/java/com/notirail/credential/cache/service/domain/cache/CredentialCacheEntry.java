package com.notirail.credential.cache.service.domain.cache;

import java.time.Instant;
import java.util.List;

public record CredentialCacheEntry(
    String credentialId,
    String keyId,
    String secretHash,
    String partnerId,
    String projectId,
    List<String> scopes,
    Instant expiresAt
) {

}
