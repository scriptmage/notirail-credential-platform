package com.notirail.credential.cache.service.domain.grpc;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GrpcExceptionInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        final ServerCall<ReqT, RespT> call,
        final Metadata headers,
        final ServerCallHandler<ReqT, RespT> next
    ) {
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(next.startCall(call, headers)) {

            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (final RuntimeException e) {
                    handleException(call, e);
                }
            }

        };
    }

    private <ReqT, RespT> void handleException(final ServerCall<ReqT, RespT> call, final RuntimeException e) {
        log.error("gRPC call failed", e);
        call.close(Status.INTERNAL.withDescription(e.getMessage()), new Metadata());
    }

}
