package com.notirail.credential.service.exception;

import java.util.UUID;

public class CredentialNotFoundException extends RuntimeException {

    public CredentialNotFoundException(final UUID credentialId) {
        super("Credential not found: %s".formatted(credentialId));
    }

}
