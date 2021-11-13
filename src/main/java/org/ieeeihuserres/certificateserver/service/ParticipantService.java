package org.ieeeihuserres.certificateserver.service;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.ieeeihuserres.certificateserver.config.CertificateServerConfig;
import org.ieeeihuserres.certificateserver.exception.EntityNotFoundException;
import org.ieeeihuserres.certificateserver.model.Participant;
import org.ieeeihuserres.certificateserver.repository.ParticipantRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final CertificateServerConfig config;
    private final ParticipantRepository participantRepository;

    public Try<Participant> findParticipant(String participantEmail) {
        return Option.ofOptional(participantRepository.findByEmail(participantEmail))
                .toTry(() -> new EntityNotFoundException("Participant not found"));
    }

}
