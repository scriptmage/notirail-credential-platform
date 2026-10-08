package com.notirail.credential.cache.service.config;

import static java.lang.Integer.MAX_VALUE;

import com.notirail.credential.cache.service.config.GrpcConfiguration.GrpcConfigurationProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(GrpcConfigurationProperties.class)
public class GrpcConfiguration {

    @ConfigurationProperties(prefix = "grpc.server")
    public record GrpcConfigurationProperties(
        @NotBlank
        String host,

        @Min(9090)
        @Max(9099)
        int port,

        @Min(1)
        @Max(MAX_VALUE)
        int maximumMessageSize
    ) {

    }

}
