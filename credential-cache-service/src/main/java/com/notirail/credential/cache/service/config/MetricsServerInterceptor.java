package com.notirail.credential.cache.service.config;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class MetricsServerInterceptor implements ServerInterceptor {

    private final Timer grpcCallTimer;

    public MetricsServerInterceptor(final MeterRegistry meterRegistry) {
        this.grpcCallTimer = Timer.builder("grpc.server.call.duration")
            .description("gRPC call duration")
            .register(meterRegistry);
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        final ServerCall<ReqT, RespT> call,
        final Metadata headers,
        final ServerCallHandler<ReqT, RespT> next
    ) {
        return grpcCallTimer.record(() -> next.startCall(call, headers));
    }

}
