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

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    // Solo Lectura: Exclusivo para el Auditor y Admin
    @GetMapping("/events")
    @PreAuthorize("hasAnyAuthority('Auditor', 'Admin')")
    public ResponseEntity<Page<AuditEvent>> getAllAuditEvents(
            @PageableDefault(size = 20, sort = "receivedAt") Pageable pageable) {
        return ResponseEntity.ok(auditService.getAllEvents(pageable));
    }

    @GetMapping("/events/topic/{topicName}")
    @PreAuthorize("hasAnyAuthority('Auditor', 'Admin')")
    public ResponseEntity<Page<AuditEvent>> getAuditEventsByTopic(
            @PathVariable String topicName,
            @PageableDefault(size = 20, sort = "receivedAt") Pageable pageable) {
        return ResponseEntity.ok(auditService.getEventsByTopic(topicName, pageable));
    }

    @GetMapping("/events/{id}")
    @PreAuthorize("hasAnyAuthority('Auditor', 'Admin')")
    public ResponseEntity<AuditEvent> getAuditEventById(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.getEventById(id));
    }
}