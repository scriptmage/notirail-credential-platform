package com.notirail.credential.service.exception;

public class CredentialKeyAlreadyRevokedException extends RuntimeException {

    public CredentialKeyAlreadyRevokedException(final String keyId) {
        super("Credential key is already revoked: %s".formatted(keyId));
    }

}
