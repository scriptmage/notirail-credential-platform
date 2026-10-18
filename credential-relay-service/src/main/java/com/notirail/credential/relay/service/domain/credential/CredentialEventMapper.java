package com.notirail.credential.relay.service.domain.credential;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import com.notirail.credential.cache.service.proto.ApplyResult;
import com.notirail.credential.cache.service.proto.CredentialEvent;
import com.notirail.credential.cache.service.proto.CredentialEventType;
import com.notirail.credential.cache.service.proto.CredentialSnapshot;
import com.notirail.credential.relay.service.domain.event.OutboxEventEntity;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CredentialEventMapper {

    private static final String EVENT_CREATED = "CredentialCreated";
    private static final String EVENT_REVOKED = "CredentialRevoked";
    private static final String EVENT_SCOPES_CHANGED = "CredentialScopesChanged";
    private static final String EVENT_BINDING_CHANGED = "CredentialBindingChanged";

    private final ObjectMapper objectMapper;

    public CredentialEvent toProto(final OutboxEventEntity event) {
        try {
            final JsonNode payload = objectMapper.readTree(event.getPayload());

            return CredentialEvent.newBuilder()
                .setEventId(event.getEventId().toString())
                .setEventType(resolveEventType(event.getEventType()))
                .setCredentialId(text(payload, "credentialId"))
                .setKeyId(text(payload, "keyId"))
                .setSnapshot(toSnapshot(payload))
                .setOccurredAt(toTimestamp(event.getCreatedAt()))
                .build();
        } catch (final Exception exception) {
            throw new IllegalStateException("Could not map outbox event: " + event.getEventId(), exception);
        }
    }

    public ApplyResult toResult(final String resultName) {
        return ApplyResult.valueOf(resultName);
    }

    private CredentialEventType resolveEventType(final String eventType) {
        return switch (eventType) {
            case EVENT_CREATED -> CredentialEventType.CREDENTIAL_CREATED;
            case EVENT_REVOKED -> CredentialEventType.CREDENTIAL_REVOKED;
            case EVENT_SCOPES_CHANGED, EVENT_BINDING_CHANGED -> CredentialEventType.CREDENTIAL_UPDATED;
            default -> throw new IllegalArgumentException("Unsupported credential event type: " + eventType);
        };
    }

    private CredentialSnapshot toSnapshot(final JsonNode payload) {
        final CredentialSnapshot.Builder builder = CredentialSnapshot.newBuilder()
            .setCredentialId(text(payload, "credentialId"))
            .setKeyId(text(payload, "keyId"))
            .setSecretHash(text(payload, "secretHash"))
            .setPartnerId(text(payload, "partnerId"));

        if (payload.hasNonNull("projectId")) {
            builder.setProjectId(payload.get("projectId").asText());
        }

        if (payload.hasNonNull("scopes")) {
            builder.addAllScopes(toStringList(payload.get("scopes")));
        }

        if (payload.hasNonNull("expiresAt")) {
            builder.setExpiresAt(toTimestamp(OffsetDateTime.parse(payload.get("expiresAt").asText())));
        }

        return builder.build();
    }

    private List<String> toStringList(final JsonNode node) {
        final List<String> values = new ArrayList<>();

        node.forEach(item -> values.add(item.asText()));

        return values;
    }

    private String text(final JsonNode payload, final String field) {
        final JsonNode value = payload.get(field);
        return value == null || value.isNull() ? "" : value.asText();
    }

    private Timestamp toTimestamp(final OffsetDateTime value) {
        return Timestamps.fromMillis(value.toInstant().toEpochMilli());
    }
}
