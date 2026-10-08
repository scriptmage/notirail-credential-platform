docker run --rm -i --network notirail_notirail ghcr.io/fullstorydev/grpcurl -plaintext -d @ \
credential-cache-service:9090 com.notirail.credential.cache.service.proto.CredentialCacheService.ApplyCredentialEvent < \
../src/test/resources/grpc/request/credential-created.json
