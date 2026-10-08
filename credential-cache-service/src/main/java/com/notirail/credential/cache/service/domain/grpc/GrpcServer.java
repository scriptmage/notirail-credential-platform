package com.notirail.credential.cache.service.domain.grpc;

import static java.util.Objects.nonNull;

import com.notirail.credential.cache.service.config.GrpcConfiguration.GrpcConfigurationProperties;
import com.notirail.credential.cache.service.service.CredentialCacheService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionServiceV1;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcServer {

    private final GrpcConfigurationProperties configuration;

    private final CredentialCacheService credentialCacheService;

    private final GrpcHealthCheck grpcHealthCheck;

    private final GrpcLoggingInterceptor loggingInterceptor;

    private Server server;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        try {
            server = ServerBuilder.forPort(configuration.port())
                .addService(credentialCacheService)
                .addService(grpcHealthCheck)
                .intercept(loggingInterceptor)
                .addService(ProtoReflectionServiceV1.newInstance())
                .maxInboundMessageSize(configuration.maximumMessageSize())
                .build()
                .start();

            log.info("gRPC server started on port {}", configuration.port());

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutting down gRPC server...");
                GrpcServer.this.stop();
            }));

        } catch (IOException e) {
            log.error("Failed to start gRPC server on port {}", configuration.port(), e);
            throw new RuntimeException("Failed to start gRPC server", e);
        }
    }

    @PreDestroy
    public void stop() {
        final Server localServer = server;
        if (nonNull(localServer)) {
            log.info("Stopping gRPC server...");
            grpcHealthCheck.setServing(false);
            localServer.shutdown();
            try {
                if (!localServer.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.warn("gRPC server did not terminate gracefully, forcing shutdown");
                    localServer.shutdownNow();
                }
            } catch (final InterruptedException e) {
                log.warn("Interrupted while waiting for gRPC server to terminate", e);
                localServer.shutdownNow();
                Thread.currentThread().interrupt();
            } finally {
                server = null;
            }
        }
    }

}
