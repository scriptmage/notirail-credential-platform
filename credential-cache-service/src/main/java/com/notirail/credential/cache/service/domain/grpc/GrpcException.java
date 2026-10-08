package com.notirail.credential.cache.service.domain.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public final class GrpcException {

    private GrpcException() {
    }

    public static StatusRuntimeException notFound(final String message) {
        return Status.NOT_FOUND.withDescription(message).asRuntimeException();
    }

    public static StatusRuntimeException invalidArgument(final String message) {
        return Status.INVALID_ARGUMENT.withDescription(message).asRuntimeException();
    }

    public static StatusRuntimeException internal(final String message) {
        return Status.INTERNAL.withDescription(message).asRuntimeException();
    }

    public static StatusRuntimeException unavailable(final String message) {
        return Status.UNAVAILABLE.withDescription(message).asRuntimeException();
    }

    public static StatusRuntimeException permissionDenied(final String message) {
        return Status.PERMISSION_DENIED.withDescription(message).asRuntimeException();
    }

    public static StatusRuntimeException unauthenticated(final String message) {
        return Status.UNAUTHENTICATED.withDescription(message).asRuntimeException();
    }

}
