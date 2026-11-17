package cl.barriodigital.audit.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * QUÉ ES: entidad JPA; cada instancia es una fila de la tabla Oracle AUDIT_EVENTS.
 *
 * QUÉ GUARDA: una copia fiel de cada mensaje que llegó desde Kafka (el JSON completo en PAYLOAD) más
 * los datos de dónde vino (tópico, partición, offset y clave) y cuándo se recibió.
 */
@Entity
// La restricción única (tópico + partición + offset) impide a nivel de base de datos guardar dos veces el
// mismo mensaje de Kafka: es la segunda barrera de idempotencia (la primera está en el listener).
@Table(name = "AUDIT_EVENTS", uniqueConstraints = @UniqueConstraint(
        name = "UK_AUDIT_KAFKA_COORDS", columnNames = { "TOPIC_NAME", "PARTITION_NUM", "OFFSET_NUM" }))
// Lombok: genera getters, setters, constructores (vacío exigido por JPA y completo) y el patrón Builder.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {

    // Clave primaria. Oracle no tiene AUTO_INCREMENT clásico: se usa una secuencia (AUDIT_SEQ).
    // allocationSize = 1 pide un número por inserción, igual al incremento de la secuencia en Oracle.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_seq")
    @SequenceGenerator(name = "audit_seq", sequenceName = "AUDIT_SEQ", allocationSize = 1)
    private Long id;

    // Nombre del tópico de origen: "requests.events" o "audit.timeline".
    @Column(name = "TOPIC_NAME", nullable = false)
    private String topicName;

    /** Partición y offset del mensaje en Kafka: identifican el evento y evitan registrarlo dos veces. */
    @Column(name = "PARTITION_NUM")
    private Integer partitionNumber;

    // "OFFSET" es palabra reservada en SQL, por eso la columna se llama OFFSET_NUM.
    @Column(name = "OFFSET_NUM")
    private Long recordOffset;

    // Clave del mensaje en Kafka (aquí, el id de la solicitud); sirve para seguir una solicitud completa.
    @Column(name = "EVENT_KEY")
    private String eventKey;

    // @Lob = objeto grande (CLOB en Oracle): el JSON puede superar los 4000 caracteres de un VARCHAR2.
    @Lob
    @Column(name = "PAYLOAD", nullable = false)
    private String payload;

    // Momento en que este servicio recibió el mensaje (no cuándo ocurrió el hecho original).
    @Column(name = "RECEIVED_AT", nullable = false)
    private LocalDateTime receivedAt;
}
