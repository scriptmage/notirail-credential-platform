package com.notirail.credential.relay.service.service;

import com.notirail.credential.relay.service.domain.event.OutboxEventEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxRelayService {

    private final OutboxDeliveryService deliveryService;

    public void pollAndDeliver() {
        final List<OutboxEventEntity> events = deliveryService.claimBatch();
        for (final OutboxEventEntity event : events) {
            deliveryService.deliver(event);
        }
    }

}
