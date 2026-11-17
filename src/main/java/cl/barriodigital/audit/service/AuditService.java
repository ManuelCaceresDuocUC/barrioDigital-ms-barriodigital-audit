package cl.barriodigital.audit.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.barriodigital.audit.entity.AuditEvent;
import cl.barriodigital.audit.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;

/**
 * QUÉ ES: capa de servicio (lógica de aplicación) de las consultas de auditoría.
 *
 * QUÉ HACE: hoy solo delega en el repositorio, pero es el lugar donde iría cualquier regla de negocio y
 * mantiene al controlador libre de acceso a datos.
 */
@Service
@RequiredArgsConstructor
// Todas las consultas corren en transacciones de solo lectura (más eficiente y evita escrituras accidentales).
@Transactional(readOnly = true)
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    // Todos los eventos, con la paginación y el orden que pida el cliente.
    public Page<AuditEvent> getAllEvents(Pageable pageable) {
        return auditEventRepository.findAll(pageable);
    }

    // Eventos de un solo tópico.
    public Page<AuditEvent> getEventsByTopic(String topic, Pageable pageable) {
        return auditEventRepository.findByTopicName(topic, pageable);
    }

    // Un evento por ID; si no existe se lanza una excepción (se traduce a error HTTP 500 en esta versión).
    public AuditEvent getEventById(Long id) {
        return auditEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de auditoria no encontrado con ID: " + id));
    }
}
