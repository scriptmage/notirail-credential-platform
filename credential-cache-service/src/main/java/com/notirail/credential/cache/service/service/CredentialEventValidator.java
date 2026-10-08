package com.notirail.credential.cache.service.service;

import static com.notirail.credential.cache.service.proto.CredentialEventType.CREDENTIAL_UNSPECIFIED;
import static java.util.Objects.isNull;

import com.notirail.credential.cache.service.proto.CredentialEvent;
import com.notirail.credential.cache.service.proto.CredentialEventType;
import com.notirail.credential.cache.service.proto.CredentialSnapshot;
import org.springframework.stereotype.Component;

@Component
public class CredentialEventValidator {

    public void validate(final CredentialEvent request) {
        requireNotBlank(request.getEventId(), "event_id must not be blank");
        requireNotBlank(request.getCredentialId(), "credential_id must not be blank");
        requireNotBlank(request.getKeyId(), "key_id must not be blank");

        if (request.getEventType() == CREDENTIAL_UNSPECIFIED) {
            throw new InvalidCredentialEventException("event_type must be specified");
        }

        if (requiresSnapshot(request.getEventType()) && !request.hasSnapshot()) {
            throw new InvalidCredentialEventException("snapshot is required for this event type");
        }

        if (!request.hasOccurredAt()) {
            throw new InvalidCredentialEventException("occurred_at must be specified");
        }

        if (request.hasSnapshot()) {
            validateSnapshot(request);
        }
    }

    private void validateSnapshot(final CredentialEvent request) {
        final CredentialSnapshot snapshot = request.getSnapshot();

        requireNotBlank(snapshot.getCredentialId(), "snapshot.credential_id must not be blank");
        requireNotBlank(snapshot.getKeyId(), "snapshot.key_id must not be blank");
        requireNotBlank(snapshot.getSecretHash(), "snapshot.secret_hash must not be blank");
        requireNotBlank(snapshot.getPartnerId(), "snapshot.partner_id must not be blank");

        if (!snapshot.getCredentialId().equals(request.getCredentialId())) {
            throw new InvalidCredentialEventException("snapshot.credential_id must match credential_id");
        }

        if (!snapshot.getKeyId().equals(request.getKeyId())) {
            throw new InvalidCredentialEventException("snapshot.key_id must match key_id");
        }

        if (snapshot.getScopesCount() == 0) {
            throw new InvalidCredentialEventException("snapshot.scopes must not be empty");
        }

        if (snapshot.getScopesList().stream().anyMatch(scope -> isNull(scope) || scope.isBlank())) {
            throw new InvalidCredentialEventException("snapshot.scopes must not contain blank values");
        }
    }

    private boolean requiresSnapshot(final CredentialEventType eventType) {
        return switch (eventType) {
            case CREDENTIAL_CREATED,
                 CREDENTIAL_UPDATED -> true;

            case CREDENTIAL_REVOKED,
                 CREDENTIAL_UNSPECIFIED,
                 UNRECOGNIZED -> false;
        };
    }

    private void requireNotBlank(final String value, final String message) {
        if (isNull(value) || value.isBlank()) {
            throw new InvalidCredentialEventException(message);
        }
    }

    public static class InvalidCredentialEventException extends IllegalArgumentException {

        public InvalidCredentialEventException(final String message) {
            super(message);
        }

    }

}
