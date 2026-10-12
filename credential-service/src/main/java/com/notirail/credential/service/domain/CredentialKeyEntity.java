package com.notirail.credential.service.domain;

import com.notirail.credential.service.exception.CredentialKeyAlreadyRevokedException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "credential_keys")
public class CredentialKeyEntity {

    @Id
    @Column(name = "key_id", nullable = false, updatable = false)
    private String id;

    @Column(name = "secret_hash", nullable = false)
    private String secretHash;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id", nullable = false)
    private CredentialEntity credential;

    protected CredentialKeyEntity() {
    }

    public static CredentialKeyEntity create(final CredentialEntity credential, final String keyId, final String secretHash, final OffsetDateTime expiresAt) {
        final OffsetDateTime now = OffsetDateTime.now();

        final CredentialKeyEntity key = new CredentialKeyEntity();
        key.credential = credential;
        key.id = keyId;
        key.secretHash = secretHash;
        key.status = CredentialKeyStatus.ACTIVE.name();
        key.expiresAt = expiresAt;
        key.createdAt = now;
        key.updatedAt = now;

        return key;
    }

    public void revoke() {
        if (CredentialKeyStatus.REVOKED.name().equals(status)) {
            throw new CredentialKeyAlreadyRevokedException(id);
        }

        status = CredentialKeyStatus.REVOKED.name();
        revokedAt = OffsetDateTime.now();
        updatedAt = revokedAt;
    }

}
