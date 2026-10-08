package com.notirail.credential.cache.service.domain.cache;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;

import com.notirail.credential.cache.service.config.RedisConfiguration.RedisConfigurationProperties;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisCredentialCacheRepository implements CredentialCacheRepository {

    private static final String CREDENTIAL_KEY_PREFIX = "auth:credential:%s";

    private static final String REVOKED_KEY_PREFIX = "auth:revoked:%s";

    private static final String EVENT_KEY_PREFIX = "auth:event:%s";

    private static final RedisScript<Long> REVOKE_SCRIPT = RedisScript.of(
        """
            redis.call('SET', KEYS[1], '1', 'EX', ARGV[1])
            redis.call('DEL', KEYS[2])
            return 1
        """,
        Long.class
    );

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    private final RedisConfigurationProperties properties;

    @Override
    public void saveActive(final CredentialCacheEntry entry) {
        final String key = credentialKey(entry.keyId());
        try {
            final String value = objectMapper.writeValueAsString(entry);
            redisTemplate.opsForValue().set(key, value, properties.credentialTtl());
        } catch (final JacksonException exception) {
            throw new IllegalStateException("Could not serialize credential cache entry", exception);
        }
    }

    @Override
    public void revoke(final String keyId, final Duration markerTtl) {
        redisTemplate.execute(
            REVOKE_SCRIPT,
            List.of(
                revokedKey(keyId),
                credentialKey(keyId)
            ),
            String.valueOf(markerTtl.toSeconds())
        );
    }

    @Override
    public Optional<CredentialCacheEntry> findByKeyId(final String keyId) {
        final String value = redisTemplate
            .opsForValue()
            .get(credentialKey(keyId));

        if (isNull(value)) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(value, CredentialCacheEntry.class));
        } catch (final JacksonException exception) {
            throw new IllegalStateException(
                "Could not deserialize credential cache entry",
                exception
            );
        }
    }

    @Override
    public boolean isRevoked(final String keyId) {
        return TRUE.equals(redisTemplate.hasKey(revokedKey(keyId)));
    }

    @Override
    public boolean hasProcessedEvent(final String eventId) {
        return TRUE.equals(redisTemplate.hasKey(eventKey(eventId)));
    }

    @Override
    public void markEventProcessed(final String eventId, final Duration ttl) {
        redisTemplate.opsForValue().set(
            eventKey(eventId),
            "1",
            ttl
        );
    }

    private String credentialKey(final String keyId) {
        return CREDENTIAL_KEY_PREFIX.formatted(keyId);
    }

    private String revokedKey(final String keyId) {
        return REVOKED_KEY_PREFIX.formatted(keyId);
    }

    private String eventKey(final String eventId) {
        return EVENT_KEY_PREFIX.formatted(eventId);
    }

}
