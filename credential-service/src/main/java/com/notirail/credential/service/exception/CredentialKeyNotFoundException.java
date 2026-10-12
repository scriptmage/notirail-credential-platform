package com.notirail.credential.service.exception;

public class CredentialKeyNotFoundException extends RuntimeException {

    public CredentialKeyNotFoundException(final String keyId) {
        super("Credential key not found: %s".formatted(keyId));
    }

}
