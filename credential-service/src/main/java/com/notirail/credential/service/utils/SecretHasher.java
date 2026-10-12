package com.notirail.credential.service.utils;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SecretHasher {

    private final Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(16, 32, 1, 19456, 2);

    public String hash(final String secret) {
        return encoder.encode(secret);
    }

    public boolean matches(final String secret, final String hash) {
        return encoder.matches(secret, hash);
    }

}
