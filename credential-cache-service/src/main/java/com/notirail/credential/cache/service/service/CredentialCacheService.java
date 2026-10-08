package com.notirail.credential.cache.service.service;

import com.notirail.credential.cache.service.domain.grpc.GrpcException;
import com.notirail.credential.cache.service.proto.ApplyCredentialEventResponse;
import com.notirail.credential.cache.service.proto.CredentialCacheServiceGrpc;
import com.notirail.credential.cache.service.proto.CredentialEvent;
import com.notirail.credential.cache.service.service.CredentialEventService.ApplyResult;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CredentialCacheService extends CredentialCacheServiceGrpc.CredentialCacheServiceImplBase {

    private final CredentialEventService eventService;

    private final CredentialEventMapper mapper;

    private final CredentialEventValidator validator;

    @Override
    public void applyCredentialEvent(final CredentialEvent request, final StreamObserver<ApplyCredentialEventResponse> responseObserver) {
        try {
            validator.validate(request);

            final ApplyResult result = eventService.apply(
                mapper.toDomain(request)
            );

            final ApplyCredentialEventResponse response = ApplyCredentialEventResponse.newBuilder()
                .setEventId(request.getEventId())
                .setResult(mapper.toProto(result))
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (final IllegalArgumentException exception) {
            log.warn("Invalid credential event: eventId={}", request.getEventId(), exception);
            responseObserver.onError(
                GrpcException.invalidArgument(exception.getMessage())
            );
        } catch (final Exception exception) {
            log.error("Could not apply credential event: eventId={}", request.getEventId(), exception);
            responseObserver.onError(
                GrpcException.internal("Could not apply credential event")
            );
        }
    }

}
