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

/**
 * Guarda cada mensaje de requests.events y audit.timeline tal como llegó. Un mensaje se identifica por su
 * tópico, partición y offset: si Kafka lo entrega de nuevo, no se registra otra vez. Un fallo de base de datos
 * se propaga y lo reintenta el manejador de errores.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAuditEventListener {

    private final AuditEventRepository auditEventRepository;

    @KafkaListener(
        topics = {"requests.events", "audit.timeline"},
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeAuditEvents(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {

        if (auditEventRepository.existsByTopicNameAndPartitionNumberAndRecordOffset(topic, partition, offset)) {
            log.info("Evento [{}-{}@{}] ya auditado: se ignora", topic, partition, offset);
            return;
        }

        AuditEvent auditEvent = AuditEvent.builder()
                .topicName(topic)
                .partitionNumber(partition)
                .recordOffset(offset)
                .eventKey(key)
                .payload(payload)
                .receivedAt(LocalDateTime.now())
                .build();

        auditEventRepository.save(auditEvent);
        log.info("Evento [{}-{}@{}] auditado con ID {}", topic, partition, offset, auditEvent.getId());
    }
}
