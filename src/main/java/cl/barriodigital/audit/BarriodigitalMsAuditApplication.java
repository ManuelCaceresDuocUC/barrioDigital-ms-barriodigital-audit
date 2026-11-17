package cl.barriodigital.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * QUÉ ES: punto de entrada del microservicio de auditoría (ms-audit).
 *
 * QUÉ HACE: este servicio escucha los tópicos de Kafka "requests.events" y "audit.timeline", guarda cada
 * mensaje en Oracle (tabla AUDIT_EVENTS) y lo expone en modo solo lectura para los roles Auditor y Admin.
 *
 * @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan: Spring escanea este
 * paquete (cl.barriodigital.audit) y todos sus subpaquetes buscando controladores, servicios, listeners, etc.
 */
@SpringBootApplication
public class BarriodigitalMsAuditApplication {

	public static void main(String[] args) {
		// Arranca el contexto de Spring, el servidor web embebido (puerto 8084) y los listeners de Kafka.
		SpringApplication.run(BarriodigitalMsAuditApplication.class, args);
	}

}
