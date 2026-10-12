package com.notirail.credential.service.credentials;

import com.notirail.credential.api.model.CredentialResponse;
import com.notirail.credential.service.api.CredentialApiMapper;
import com.notirail.credential.service.credentials.key.CredentialKeyCreationResult;
import com.notirail.credential.service.credentials.key.CredentialKeyRepository;
import com.notirail.credential.service.domain.CredentialEntity;
import com.notirail.credential.service.domain.CredentialKeyEntity;
import com.notirail.credential.service.domain.CredentialStatus;
import com.notirail.credential.service.exception.CredentialKeyNotFoundException;
import com.notirail.credential.service.exception.CredentialNotFoundException;
import com.notirail.credential.service.utils.CredentialKeyGenerator;
import com.notirail.credential.service.utils.SecretHasher;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CredentialService {

    private final CredentialRepository credentialRepository;

    private final CredentialKeyRepository credentialKeyRepository;

    private final CredentialKeyGenerator keyGenerator;

    private final SecretHasher secretHasher;

    private final CredentialApiMapper credentialApiMapper;

    @Transactional
    public CredentialKeyCreationResult createCredential(final String partnerId, final String projectId, final Set<String> scopes, final OffsetDateTime expiresAt) {
        final CredentialEntity credential = CredentialEntity.create(partnerId, projectId, scopes, expiresAt);
        final CredentialEntity savedCredential = credentialRepository.save(credential);
        return createCredentialKey(savedCredential, expiresAt);
    }

    @Transactional
    public CredentialKeyCreationResult createCredentialKey(final UUID credentialId, final OffsetDateTime expiresAt) {
        final CredentialEntity credential = credentialRepository.findById(credentialId)
            .orElseThrow(() -> new CredentialNotFoundException(credentialId));

        return createCredentialKey(credential, expiresAt);
    }

    private CredentialKeyCreationResult createCredentialKey(final CredentialEntity credential, final OffsetDateTime expiresAt) {
        final String keyId = keyGenerator.generateKeyId();
        final String rawSecret = keyGenerator.generateSecret();

        final CredentialKeyEntity key = CredentialKeyEntity.create(
            credential,
            keyId,
            secretHasher.hash(rawSecret),
            expiresAt
        );

        final CredentialKeyEntity savedKey = credentialKeyRepository.save(key);

        return new CredentialKeyCreationResult(
            credential.getId(),
            savedKey.getId(),
            keyGenerator.toBearerCredential(savedKey.getId(), rawSecret),
            savedKey
        );
    }

    @Transactional(readOnly = true)
    public CredentialResponse getCredential(final UUID credentialId) {
        final CredentialEntity credential = credentialRepository.findById(credentialId)
            .orElseThrow(() -> new CredentialNotFoundException(credentialId));

        return credentialApiMapper.toResponse(credential);
    }

    @Transactional
    public CredentialEntity updateCredential(
        final UUID credentialId,
        final String projectId,
        final Set<String> scopes,
        final OffsetDateTime expiresAt,
        final CredentialStatus status
    ) {
        final CredentialEntity credential = credentialRepository.findById(credentialId)
            .orElseThrow(() -> new CredentialNotFoundException(credentialId));

        credential.update(projectId, scopes, expiresAt, status);

        return credentialRepository.save(credential);
    }

    @Transactional
    public CredentialKeyEntity revokeCredentialKey(final UUID credentialId, final String keyId) {
        final CredentialKeyEntity key = credentialKeyRepository
            .findByCredential_IdAndId(credentialId, keyId)
            .orElseThrow(() -> new CredentialKeyNotFoundException(keyId));

        key.revoke();

        return credentialKeyRepository.save(key);
    }

    @Transactional(readOnly = true)
    public List<CredentialKeyEntity> listCredentialKeys(final UUID credentialId) {
        if (!credentialRepository.existsById(credentialId)) {
            throw new CredentialNotFoundException(credentialId);
        }

        return credentialKeyRepository.findByCredential_Id(credentialId);
    }

    @Transactional(readOnly = true)
    public CredentialKeyEntity getCredentialKey(final UUID credentialId, final String keyId) {
        if (!credentialRepository.existsById(credentialId)) {
            throw new CredentialNotFoundException(credentialId);
        }

        return credentialKeyRepository.findByCredential_IdAndId(credentialId, keyId)
            .orElseThrow(() -> new CredentialKeyNotFoundException(keyId));
    }

}
