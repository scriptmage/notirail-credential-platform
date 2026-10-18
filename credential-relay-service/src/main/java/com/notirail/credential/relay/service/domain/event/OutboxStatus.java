package com.notirail.credential.relay.service.domain.event;

public enum OutboxStatus {
    PENDING,
    CLAIMED,
    RETRY_WAITING,
    DELIVERED,
    DEAD
}
