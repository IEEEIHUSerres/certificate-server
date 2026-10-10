package org.ieeeihuserres.certificateserver.service;

import io.vavr.control.Try;
import org.ieeeihuserres.certificateserver.config.CertificateServerConfig;
import org.ieeeihuserres.certificateserver.exception.EntityNotFoundException;
import org.ieeeihuserres.certificateserver.model.Participant;
import org.ieeeihuserres.certificateserver.repository.ParticipantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Test
    void returnsParticipantWhenFound() {
        final Participant participant = new Participant("John", "Doe", "john@example.org");
        when(participantRepository.findByEmail("john@example.org")).thenReturn(Optional.of(participant));
        final ParticipantService service = new ParticipantService(new CertificateServerConfig(), participantRepository);

        final Try<Participant> result = service.findParticipant("john@example.org");

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get()).isEqualTo(participant);
    }

    @Test
    void failsWithEntityNotFoundWhenMissing() {
        when(participantRepository.findByEmail("nobody@example.org")).thenReturn(Optional.empty());
        final ParticipantService service = new ParticipantService(new CertificateServerConfig(), participantRepository);

        final Try<Participant> result = service.findParticipant("nobody@example.org");

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Participant not found");
    }
}
