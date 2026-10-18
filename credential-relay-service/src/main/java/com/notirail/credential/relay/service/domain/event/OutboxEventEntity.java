package com.notirail.credential.relay.service.domain.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "outbox_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEventEntity {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, updatable = false)
    private String eventType;

    @Column(name = "aggregate_type", nullable = false, updatable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, updatable = false)
    private UUID aggregateId;

    @Column(name = "payload", nullable = false, updatable = false, columnDefinition = "jsonb")
    private String payload;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutboxStatus status;

    @Setter
    @Column(name = "available_at", nullable = false)
    private OffsetDateTime availableAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "claimed_at")
    private OffsetDateTime claimedAt;

    @Column(name = "delivered_at")
    private OffsetDateTime deliveredAt;

    @Setter
    @Column(name = "last_error")
    private String lastError;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public void claim() {
        this.status = OutboxStatus.CLAIMED;
    }

    public void markDelivered() {
        this.status = OutboxStatus.DELIVERED;
        this.lastError = null;
    }

    public void markRetry(final OffsetDateTime availableAt, final String lastError) {
        this.status = OutboxStatus.RETRY_WAITING;
        this.availableAt = availableAt;
        this.lastError = lastError;
    }

    public void markDead(final String lastError) {
        this.status = OutboxStatus.DEAD;
        this.availableAt = OffsetDateTime.now();
        this.lastError = lastError;
    }

}
