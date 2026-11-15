package cl.barriodigital.audit.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import cl.barriodigital.audit.entity.AuditEvent;
import cl.barriodigital.audit.repository.AuditEventRepository;

class KafkaAuditEventListenerTest {

    private final AuditEventRepository repository = mock(AuditEventRepository.class);
    private final KafkaAuditEventListener listener = new KafkaAuditEventListener(repository);

    @Test
    void guardaElEventoConSuTopicoParticionYOffset() {
        listener.consumeAuditEvents("{\"requestId\":7}", "audit.timeline", 2, 15L, "7");

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(repository).save(captor.capture());
        AuditEvent saved = captor.getValue();
        assertThat(saved.getTopicName()).isEqualTo("audit.timeline");
        assertThat(saved.getPartitionNumber()).isEqualTo(2);
        assertThat(saved.getRecordOffset()).isEqualTo(15L);
        assertThat(saved.getEventKey()).isEqualTo("7");
        assertThat(saved.getPayload()).isEqualTo("{\"requestId\":7}");
        assertThat(saved.getReceivedAt()).isNotNull();
    }

    @Test
    void unMensajeRepetidoNoSeRegistraDosVeces() {
        when(repository.existsByTopicNameAndPartitionNumberAndRecordOffset("requests.events", 0, 3L)).thenReturn(true);

        listener.consumeAuditEvents("{}", "requests.events", 0, 3L, "7");

        verify(repository, never()).save(any());
    }

    @Test
    void unEventoSinClaveSeGuardaIgual() {
        listener.consumeAuditEvents("{}", "requests.events", 0, 4L, null);

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getEventKey()).isNull();
    }

    @Test
    void unFalloDeBaseDeDatosSePropagaParaQueSeReintente() {
        when(repository.save(any())).thenThrow(new IllegalStateException("Oracle caído"));

        assertThatThrownBy(() -> listener.consumeAuditEvents("{}", "requests.events", 0, 5L, "7"))
                .isInstanceOf(IllegalStateException.class);
    }
}
