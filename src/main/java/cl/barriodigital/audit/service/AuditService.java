package cl.barriodigital.audit.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.barriodigital.audit.entity.AuditEvent;
import cl.barriodigital.audit.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public Page<AuditEvent> getAllEvents(Pageable pageable) {
        return auditEventRepository.findAll(pageable);
    }

    public Page<AuditEvent> getEventsByTopic(String topic, Pageable pageable) {
        return auditEventRepository.findByTopicName(topic, pageable);
    }

    public AuditEvent getEventById(Long id) {
        return auditEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de auditoria no encontrado con ID: " + id));
    }
}