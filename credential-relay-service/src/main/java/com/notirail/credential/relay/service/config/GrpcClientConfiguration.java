package com.notirail.credential.relay.service.config;

import com.notirail.credential.cache.service.proto.CredentialCacheServiceGrpc;
import com.notirail.credential.relay.service.config.GrpcClientConfiguration.GrpcClientConfigurationProperties;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GrpcClientConfigurationProperties.class)
public class GrpcClientConfiguration {

    @Bean
    public ManagedChannel credentialCacheChannel(GrpcClientConfigurationProperties config) {
        return ManagedChannelBuilder
            .forAddress(config.getUrl(), config.getPort())
            .usePlaintext()
            .build();
    }

    @Bean
    public CredentialCacheServiceGrpc.CredentialCacheServiceBlockingStub credentialCacheServiceStub(
        final ManagedChannel credentialCacheChannel
    ) {
        return CredentialCacheServiceGrpc.newBlockingStub(credentialCacheChannel);
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "remotes.credential-cache-service")
    public static class GrpcClientConfigurationProperties {

        @NotBlank
        private String url;

        @Min(9090)
        @Max(9099)
        private int port;

        private Duration deadline;

    }

}
