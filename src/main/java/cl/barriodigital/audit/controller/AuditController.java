package cl.barriodigital.audit.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.barriodigital.audit.entity.AuditEvent;
import cl.barriodigital.audit.service.AuditService;
import lombok.RequiredArgsConstructor;

/**
 * QUÉ ES: controlador REST (capa de entrada HTTP) del servicio de auditoría.
 *
 * QUÉ HACE: expone consultas de solo lectura sobre los eventos auditados. Todas las rutas cuelgan de
 * /api/audit y exigen el rol Auditor o Admin (el rol viene en el claim "roles" del JWT de Azure).
 * No contiene lógica: delega en {@link AuditService}.
 */
@RestController
@RequestMapping("/api/audit")
// Lombok genera el constructor con los campos final: así Spring inyecta el servicio sin @Autowired.
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    // Solo Lectura: Exclusivo para el Auditor y Admin
    // GET /api/audit/events?page=0&size=20&sort=receivedAt,desc -> lista paginada de todos los eventos.
    @GetMapping("/events")
    // @PreAuthorize se evalúa antes de ejecutar el método: sin el rol responde 403.
    @PreAuthorize("hasAnyAuthority('Auditor', 'Admin')")
    public ResponseEntity<Page<AuditEvent>> getAllAuditEvents(
            // Si el cliente no envía paginación: 20 por página, ordenado por fecha de recepción.
            @PageableDefault(size = 20, sort = "receivedAt") Pageable pageable) {
        return ResponseEntity.ok(auditService.getAllEvents(pageable));
    }

    // GET /api/audit/events/topic/requests.events -> solo los eventos de un tópico de Kafka.
    @GetMapping("/events/topic/{topicName}")
    @PreAuthorize("hasAnyAuthority('Auditor', 'Admin')")
    public ResponseEntity<Page<AuditEvent>> getAuditEventsByTopic(
            @PathVariable String topicName,
            @PageableDefault(size = 20, sort = "receivedAt") Pageable pageable) {
        return ResponseEntity.ok(auditService.getEventsByTopic(topicName, pageable));
    }

    // GET /api/audit/events/15 -> el detalle de un evento por su ID.
    @GetMapping("/events/{id}")
    @PreAuthorize("hasAnyAuthority('Auditor', 'Admin')")
    public ResponseEntity<AuditEvent> getAuditEventById(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.getEventById(id));
    }
}
