package com.notirail.credential.service.domain;

import static java.util.Objects.nonNull;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "credentials")
public class CredentialEntity {

    @Id
    @Column(name = "credential_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "partner_id", nullable = false)
    private String partnerId;

    @Column(name = "project_id")
    private String projectId;

    @Column(name = "status", nullable = false)
    private String status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "scopes", nullable = false)
    private Set<String> scopes = new LinkedHashSet<>();

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "credential", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CredentialKeyEntity> keys = new ArrayList<>();

    protected CredentialEntity() {
    }

    public static CredentialEntity create(final String partnerId, final String projectId, final Set<String> scopes, final OffsetDateTime expiresAt) {
        final OffsetDateTime now = OffsetDateTime.now();

        final CredentialEntity credential = new CredentialEntity();
        credential.id = UUID.randomUUID();
        credential.partnerId = partnerId;
        credential.projectId = projectId;
        credential.status = CredentialStatus.ACTIVE.name();
        credential.scopes = new LinkedHashSet<>(scopes);
        credential.expiresAt = expiresAt;
        credential.createdAt = now;
        credential.updatedAt = now;

        return credential;
    }

    public void update(final String projectId, final Set<String> scopes, final OffsetDateTime expiresAt, final CredentialStatus status) {
        if (nonNull(projectId)) {
            this.projectId = projectId;
        }

        if (nonNull(scopes)) {
            this.scopes = new LinkedHashSet<>(scopes);
        }

        if (nonNull(expiresAt)) {
            this.expiresAt = expiresAt;
        }

        if (nonNull(status)) {
            this.status = status.name();
        }

        this.updatedAt = OffsetDateTime.now();
    }

}
