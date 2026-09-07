# Proyecto: BarrioDigital - Servicio de Auditoría

**Componente:** Microservicio de Dominio (Trazabilidad)
**Integrantes:** [Nombre Apellido 1], [Nombre Apellido 2], [Nombre Apellido 3]

## Descripción
Garantiza la transparencia y trazabilidad de las operaciones en BarrioDigital. Construye y mantiene un "Timeline" inmutable de todo lo que ocurre con un trámite: quién lo ingresó, quién lo admitió, quién realizó la visita o lo resolvió, y en qué fecha/hora. Provee datos cruciales de solo lectura para los usuarios con rol Auditor.

## Tecnologías a usar
* **Framework:** Spring Boot (Java)
* **Base de Datos:** Oracle
* **Streaming (Consumer):** Apache Kafka (Tópico: `audit.timeline`)
* **Integración:** API REST (Endpoints de solo lectura)
