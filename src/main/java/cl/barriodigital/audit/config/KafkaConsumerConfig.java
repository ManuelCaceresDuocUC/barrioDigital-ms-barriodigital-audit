package cl.barriodigital.audit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * QUÉ ES: clase de configuración de Spring (@Configuration) para el consumo de Kafka.
 *
 * QUÉ HACE: define cómo se reacciona cuando un @KafkaListener lanza una excepción. Spring Kafka detecta
 * automáticamente un bean de tipo DefaultErrorHandler y lo aplica a todos los listeners del servicio.
 */
@Configuration
public class KafkaConsumerConfig {

    /** Reintenta 3 veces (1 s entre intentos) un mensaje que falla; luego lo registra y sigue con el siguiente. */
    @Bean
    DefaultErrorHandler kafkaErrorHandler() {
        // FixedBackOff(intervalo en ms, cantidad de reintentos): espera fija de 1 segundo entre intentos.
        // Si tras los 3 reintentos sigue fallando, el mensaje se descarta (con log) para no bloquear la partición.
        return new DefaultErrorHandler(new FixedBackOff(1_000L, 3L));
    }
}
