package cl.barriodigital.audit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.barriodigital.audit.entity.AuditEvent;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    
    // Búsqueda por tópico
    Page<AuditEvent> findByTopicName(String topicName, Pageable pageable);
}