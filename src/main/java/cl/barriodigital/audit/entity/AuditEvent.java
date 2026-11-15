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

@Entity
@Table(name = "AUDIT_EVENTS", uniqueConstraints = @UniqueConstraint(
        name = "UK_AUDIT_KAFKA_COORDS", columnNames = { "TOPIC_NAME", "PARTITION_NUM", "OFFSET_NUM" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_seq")
    @SequenceGenerator(name = "audit_seq", sequenceName = "AUDIT_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "TOPIC_NAME", nullable = false)
    private String topicName;

    /** Partición y offset del mensaje en Kafka: identifican el evento y evitan registrarlo dos veces. */
    @Column(name = "PARTITION_NUM")
    private Integer partitionNumber;

    @Column(name = "OFFSET_NUM")
    private Long recordOffset;

    @Column(name = "EVENT_KEY")
    private String eventKey;

    @Lob
    @Column(name = "PAYLOAD", nullable = false)
    private String payload;

    @Column(name = "RECEIVED_AT", nullable = false)
    private LocalDateTime receivedAt;
}