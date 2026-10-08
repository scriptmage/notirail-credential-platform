package com.notirail.credential.cache.service.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@EnableConfigurationProperties(RedisConfiguration.RedisConfigurationProperties.class)
public class RedisConfiguration {

    @Bean
    public StringRedisTemplate stringRedisTemplate(final RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @ConfigurationProperties(prefix = "redis")
    public record RedisConfigurationProperties(
        Duration credentialTtl,
        Duration eventIdempotencyTtl,
        Duration revokedMarkerTtl
    ) {

    }

}
