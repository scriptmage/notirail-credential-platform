package com.notirail.credential.service.api;

import com.notirail.credential.api.CredentialsApi;
import com.notirail.credential.api.model.CreateCredentialRequest;
import com.notirail.credential.api.model.CredentialCreatedResponse;
import com.notirail.credential.api.model.CredentialResponse;
import com.notirail.credential.api.model.UpdateCredentialRequest;
import com.notirail.credential.service.credentials.CredentialService;
import com.notirail.credential.service.credentials.key.CredentialKeyCreationResult;
import com.notirail.credential.service.domain.CredentialEntity;
import com.notirail.credential.service.domain.CredentialStatus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CredentialController implements CredentialsApi {

    private final CredentialService credentialService;

    private final CredentialApiMapper credentialApiMapper;

    @Override
    public ResponseEntity<CredentialCreatedResponse> createCredential(final CreateCredentialRequest request) {
        final CredentialKeyCreationResult result = credentialService.createCredential(
            request.getPartnerId(),
            request.getProjectId(),
            request.getScopes(),
            request.getExpiresAt()
        );

        return ResponseEntity.status(201)
            .body(credentialApiMapper.toCreatedResponse(result));
    }

    @Override
    public ResponseEntity<CredentialResponse> getCredential(final UUID credentialId) {
        return ResponseEntity.ok(credentialService.getCredential(credentialId));
    }

    @Override
    public ResponseEntity<CredentialResponse> updateCredential(final UUID credentialId, final UpdateCredentialRequest request) {
        final CredentialEntity credential = credentialService.updateCredential(
            credentialId,
            request.getProjectId(),
            request.getScopes(),
            request.getExpiresAt(),
            request.getStatus() != null
                ? CredentialStatus.valueOf(request.getStatus().name())
                : null
        );

        return ResponseEntity.ok(credentialApiMapper.toResponse(credential));
    }

}
