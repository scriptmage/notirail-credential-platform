CredentialCacheService
→ CredentialEventValidator
→ CredentialEventMapper
→ CredentialEventService
→ CredentialCacheRepository
→ Redis


TODO:
A CredentialEventService idempotencia-kezelését
atomikus Redis Lua scriptre cserélni.

TODO:
hasProcessedEvent + apply + markEventProcessed
→ atomikus Redis Lua script

