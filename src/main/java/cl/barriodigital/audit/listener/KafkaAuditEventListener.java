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
 *
 * QUÉ ES: el CONSUMIDOR de Kafka del servicio (la pieza central de auditoría).
 * CONCEPTO: Kafka entrega "al menos una vez"; puede repetir un mensaje (por ejemplo tras un reinicio).
 * Por eso se comprueba antes de guardar si esa coordenada (tópico, partición, offset) ya existe.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAuditEventListener {

    private final AuditEventRepository auditEventRepository;

    // Spring crea un consumidor que se suscribe a ambos tópicos y llama a este método por cada mensaje.
    // groupId: los consumidores con el mismo grupo se reparten las particiones; otro grupo (como el de
    // report) recibe su propia copia de todos los mensajes. Se lee de application.yaml.
    @KafkaListener(
        topics = {"requests.events", "audit.timeline"},
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeAuditEvents(
            // Cuerpo del mensaje (JSON como texto; no se interpreta, se guarda tal cual).
            @Payload String payload,
            // Metadatos que Kafka adjunta a cada mensaje:
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            // La clave puede no venir, por eso required = false.
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {

        // Idempotencia: si ya existe un registro con las mismas coordenadas, es un reenvío: se descarta.
        if (auditEventRepository.existsByTopicNameAndPartitionNumberAndRecordOffset(topic, partition, offset)) {
            log.info("Evento [{}-{}@{}] ya auditado: se ignora", topic, partition, offset);
            return;
        }

        // Se arma la fila con el patrón Builder generado por Lombok.
        AuditEvent auditEvent = AuditEvent.builder()
                .topicName(topic)
                .partitionNumber(partition)
                .recordOffset(offset)
                .eventKey(key)
                .payload(payload)
                .receivedAt(LocalDateTime.now())
                .build();

        // INSERT en Oracle. Si falla, la excepción sube y DefaultErrorHandler reintenta (ver KafkaConsumerConfig).
        auditEventRepository.save(auditEvent);
        log.info("Evento [{}-{}@{}] auditado con ID {}", topic, partition, offset, auditEvent.getId());
    }
}
