package com.notirail.credential.relay.service.domain.event;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

    //@formatter:off
    @Query(value = """
            SELECT *
            FROM outbox_events
            WHERE (status = 'PENDING' AND available_at <= CURRENT_TIMESTAMP)
               OR (status = 'RETRY_WAITING' AND available_at <= CURRENT_TIMESTAMP)
               OR (status = 'CLAIMED' AND claimed_at < :claimDeadline)
            ORDER BY available_at, created_at
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
        """,
        nativeQuery = true
    )
    //@formatter:on
    List<OutboxEventEntity> findClaimableEvents(
        @Param("claimDeadline") final OffsetDateTime claimDeadline,
        @Param("batchSize") final int batchSize
    );

}
