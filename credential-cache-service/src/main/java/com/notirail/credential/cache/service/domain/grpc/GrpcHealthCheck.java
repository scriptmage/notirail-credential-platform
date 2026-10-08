package com.notirail.credential.cache.service.domain.grpc;

import com.notirail.credential.cache.service.proto.HealthCheckRequest;
import com.notirail.credential.cache.service.proto.HealthCheckResponse;
import com.notirail.credential.cache.service.proto.HealthGrpc.HealthImplBase;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GrpcHealthCheck extends HealthImplBase {

    private volatile boolean serving = true;

    @Override
    public void check(final HealthCheckRequest request, final StreamObserver<HealthCheckResponse> responseObserver) {
        log.debug("Health check requested for service: {}", request.getService());

        final HealthCheckResponse response = HealthCheckResponse.newBuilder()
            .setStatus(isServing())
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private HealthCheckResponse.ServingStatus isServing() {
        return serving ? HealthCheckResponse.ServingStatus.SERVING : HealthCheckResponse.ServingStatus.NOT_SERVING;
    }

    @Override
    public void watch(final HealthCheckRequest request, final StreamObserver<HealthCheckResponse> responseObserver) {
        log.debug("Health watch requested for service: {}", request.getService());

        final HealthCheckResponse response = HealthCheckResponse.newBuilder()
            .setStatus(isServing())
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void setServing(final boolean serving) {
        this.serving = serving;
        log.info("gRPC server serving status changed: {}", serving ? "SERVING" : "NOT_SERVING");
    }

}
