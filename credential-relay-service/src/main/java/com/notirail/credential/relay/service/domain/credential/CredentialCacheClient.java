package com.notirail.credential.relay.service.domain.credential;

import com.notirail.credential.cache.service.proto.ApplyCredentialEventResponse;
import com.notirail.credential.cache.service.proto.ApplyResult;
import com.notirail.credential.cache.service.proto.CredentialCacheServiceGrpc.CredentialCacheServiceBlockingStub;
import com.notirail.credential.cache.service.proto.CredentialEvent;
import com.notirail.credential.relay.service.config.GrpcClientConfiguration.GrpcClientConfigurationProperties;
import com.notirail.credential.relay.service.domain.event.OutboxEventEntity;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CredentialCacheClient {

    private final CredentialCacheServiceBlockingStub stub;

    private final CredentialEventMapper mapper;

    private final GrpcClientConfigurationProperties config;

    public DeliveryOutcome apply(final OutboxEventEntity event) {
        try {
            final CredentialEvent request = mapper.toProto(event);

            final ApplyCredentialEventResponse response = stub.withDeadlineAfter(
                config.getDeadline().toMillis(),
                TimeUnit.MILLISECONDS
            ).applyCredentialEvent(request);

            return mapResponse(response.getResult());
        } catch (final StatusRuntimeException exception) {
            log.warn(
                "Credential cache event delivery failed: eventId={}, grpcStatus={}, description={}",
                event.getEventId(),
                exception.getStatus().getCode(),
                exception.getStatus().getDescription(),
                exception
            );

            return mapError(exception.getStatus().getCode());
        }
    }

    private DeliveryOutcome mapResponse(final ApplyResult result) {
        return switch (result) {
            case APPLIED, ALREADY_APPLIED -> DeliveryOutcome.DELIVERED;
            case UNRECOGNIZED, UNSPECIFIED -> {
                log.warn("Credential cache service returned unspecified apply result");
                yield DeliveryOutcome.RETRYABLE_FAILURE;
            }
        };
    }

    private DeliveryOutcome mapError(final Code code) {
        return switch (code) {
            case INVALID_ARGUMENT,
                 UNAUTHENTICATED,
                 PERMISSION_DENIED,
                 NOT_FOUND -> DeliveryOutcome.PERMANENTLY_FAILED;

            default -> DeliveryOutcome.RETRYABLE_FAILURE;
        };
    }

    public enum DeliveryOutcome {
        DELIVERED,
        RETRYABLE_FAILURE,
        PERMANENTLY_FAILED
    }

}
