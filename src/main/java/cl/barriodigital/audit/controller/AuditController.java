package cl.barriodigital.audit.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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

    // GET /api/audit/events
    @GetMapping("/events")
    public ResponseEntity<Page<AuditEvent>> getAllAuditEvents(
            @PageableDefault(size = 20, sort = "receivedAt") Pageable pageable) {
        return ResponseEntity.ok(auditService.getAllEvents(pageable));
    }

    // GET /api/audit/events/topic/{topicName}
    @GetMapping("/events/topic/{topicName}")
    public ResponseEntity<Page<AuditEvent>> getAuditEventsByTopic(
            @PathVariable String topicName,
            @PageableDefault(size = 20, sort = "receivedAt") Pageable pageable) {
        return ResponseEntity.ok(auditService.getEventsByTopic(topicName, pageable));
    }

    // GET /api/audit/events/{id}
    @GetMapping("/events/{id}")
    public ResponseEntity<AuditEvent> getAuditEventById(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.getEventById(id));
    }
}