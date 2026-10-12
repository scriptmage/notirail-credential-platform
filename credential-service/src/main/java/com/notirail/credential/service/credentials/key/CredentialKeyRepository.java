package com.notirail.credential.service.credentials.key;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.notirail.credential.service.domain.CredentialKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialKeyRepository extends JpaRepository<CredentialKeyEntity, String> {

    List<CredentialKeyEntity> findByCredential_Id(UUID credentialId);

    Optional<CredentialKeyEntity> findByCredential_IdAndId(UUID credentialId, String keyId);

}
