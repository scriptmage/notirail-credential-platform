package com.notirail.credential.service.credentials;

import java.util.UUID;
import com.notirail.credential.service.domain.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialRepository extends JpaRepository<CredentialEntity, UUID> {

}
