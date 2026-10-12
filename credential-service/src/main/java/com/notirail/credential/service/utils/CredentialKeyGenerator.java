package com.notirail.credential.service.utils;

import java.security.SecureRandom;
import java.util.HexFormat;
import org.springframework.stereotype.Component;

@Component
public class CredentialKeyGenerator {

    private static final String KEY_ID_PREFIX = "ntr_live_";

    private static final int KEY_ID_RANDOM_BYTES = 16;

    private static final int SECRET_BYTES = 32;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateKeyId() {
        final byte[] randomBytes = new byte[KEY_ID_RANDOM_BYTES];
        secureRandom.nextBytes(randomBytes);

        return KEY_ID_PREFIX + HexFormat.of().formatHex(randomBytes);
    }

    public String generateSecret() {
        final byte[] secretBytes = new byte[SECRET_BYTES];
        secureRandom.nextBytes(secretBytes);

        return HexFormat.of().formatHex(secretBytes);
    }

    public String toBearerCredential(final String keyId, final String secret) {
        return keyId + "_" + secret;
    }

}
