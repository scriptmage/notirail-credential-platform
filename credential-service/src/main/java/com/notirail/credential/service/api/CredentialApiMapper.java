package com.notirail.credential.service.api;

import com.notirail.credential.api.model.CredentialCreatedResponse;
import com.notirail.credential.api.model.CredentialKeyCreatedResponse;
import com.notirail.credential.api.model.CredentialKeyResponse;
import com.notirail.credential.api.model.CredentialResponse;
import com.notirail.credential.service.credentials.key.CredentialKeyCreationResult;
import com.notirail.credential.service.domain.CredentialEntity;
import com.notirail.credential.service.domain.CredentialKeyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CredentialApiMapper {

    CredentialCreatedResponse toCreatedResponse(CredentialKeyCreationResult result);

    CredentialKeyCreatedResponse toCreatedKeyResponse(CredentialKeyCreationResult result);

    @Mapping(target = "credentialId", source = "id")
    @Mapping(target = "status", expression = "java(CredentialResponse.StatusEnum.fromValue(credential.getStatus()))")
    CredentialResponse toResponse(CredentialEntity credential);

    @Mapping(target = "keyId", source = "id")
    CredentialKeyResponse toKeyResponse(CredentialKeyEntity key);

}
