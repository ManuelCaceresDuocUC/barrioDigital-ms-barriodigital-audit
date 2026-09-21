package cl.barriodigital.audit.listener;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import cl.barriodigital.audit.entity.AuditEvent;
import cl.barriodigital.audit.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaAuditEventListener {

    private final AuditEventRepository auditEventRepository;

    @KafkaListener(
        topics = {"requests.events", "audit.timeline"},
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeAuditEvents(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {

        log.info("Evento recibido desde el tópico [{}]: {}", topic, payload);

        AuditEvent auditEvent = AuditEvent.builder()
                .topicName(topic)
                .eventKey(key)
                .payload(payload)
                .receivedAt(LocalDateTime.now())
                .build();

        auditEventRepository.save(auditEvent);
        log.info("Evento auditado con exito con ID: {}", auditEvent.getId());
    }
}