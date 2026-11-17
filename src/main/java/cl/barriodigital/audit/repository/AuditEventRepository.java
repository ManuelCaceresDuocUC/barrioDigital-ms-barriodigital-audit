package cl.barriodigital.audit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.barriodigital.audit.entity.AuditEvent;

/**
 * QUÉ ES: repositorio de Spring Data JPA (capa de acceso a datos).
 *
 * QUÉ HACE: al extender JpaRepository obtenemos gratis save, findById, findAll(Pageable), etc.
 * Los métodos declarados abajo NO se implementan a mano: Spring Data genera el SQL a partir del NOMBRE
 * del método (findBy + campo, existsBy + campos unidos con And).
 */
@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    
    // Búsqueda por tópico
    // SQL generado: SELECT ... FROM AUDIT_EVENTS WHERE TOPIC_NAME = ? (con paginación y orden).
    Page<AuditEvent> findByTopicName(String topicName, Pageable pageable);

    // Idempotencia: Kafka puede entregar un mismo mensaje más de una vez
    // Devuelve true si ya hay una fila con esas tres coordenadas (consulta barata, solo comprueba existencia).
    boolean existsByTopicNameAndPartitionNumberAndRecordOffset(String topicName, Integer partitionNumber,
            Long recordOffset);
}
