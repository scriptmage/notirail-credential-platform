package com.notirail.credential.service.credentials.key;

import com.notirail.credential.service.domain.CredentialKeyEntity;
import java.util.UUID;

public record CredentialKeyCreationResult(
    UUID credentialId,
    String keyId,
    String credential,
    CredentialKeyEntity key
) {

}
