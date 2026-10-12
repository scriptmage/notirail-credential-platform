package com.notirail.credential.service.api;

import static java.util.Objects.nonNull;

import com.notirail.credential.api.CredentialKeysApi;
import com.notirail.credential.api.model.CreateCredentialKeyRequest;
import com.notirail.credential.api.model.CredentialKeyCreatedResponse;
import com.notirail.credential.api.model.CredentialKeyListResponse;
import com.notirail.credential.api.model.CredentialKeyResponse;
import com.notirail.credential.service.credentials.CredentialService;
import com.notirail.credential.service.credentials.key.CredentialKeyCreationResult;
import com.notirail.credential.service.domain.CredentialKeyEntity;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CredentialKeyController implements CredentialKeysApi {

    private final CredentialService credentialService;

    private final CredentialApiMapper credentialApiMapper;

    @Override
    public ResponseEntity<CredentialKeyCreatedResponse> createCredentialKey(final UUID credentialId, final CreateCredentialKeyRequest request) {
        final OffsetDateTime expiresAt = nonNull(request) ? request.getExpiresAt() : null;
        final CredentialKeyCreationResult result = credentialService.createCredentialKey(credentialId, expiresAt);

        return ResponseEntity.status(201)
            .body(credentialApiMapper.toCreatedKeyResponse(result));
    }

    @Override
    public ResponseEntity<CredentialKeyResponse> getCredentialKey(final UUID credentialId, @Size(max = 128) final String keyId) {
        final CredentialKeyEntity key = credentialService.getCredentialKey(credentialId, keyId);
        return ResponseEntity.ok(credentialApiMapper.toKeyResponse(key));
    }

    @Override
    public ResponseEntity<CredentialKeyListResponse> listCredentialKeys(final UUID credentialId) {
        final List<CredentialKeyEntity> keys = credentialService.listCredentialKeys(credentialId);

        final CredentialKeyListResponse response = new CredentialKeyListResponse();
        response.setCredentialId(credentialId);
        response.setKeys(
            keys.stream()
                .map(credentialApiMapper::toKeyResponse)
                .toList()
        );

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CredentialKeyResponse> revokeCredentialKey(final UUID credentialId, @Size(max = 128) final String keyId) {
        final CredentialKeyEntity key = credentialService.revokeCredentialKey(credentialId, keyId);
        return ResponseEntity.ok(credentialApiMapper.toKeyResponse(key));
    }

}
