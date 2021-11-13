package org.ieeeihuserres.certificateserver.controller;

import lombok.RequiredArgsConstructor;
import org.ieeeihuserres.certificateserver.service.CertificateService;
import org.ieeeihuserres.certificateserver.service.ParticipantService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final ParticipantService participantService;

    @GetMapping(value = "/generate/{eMail}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> generateCertificate(@PathVariable("eMail") String eMail) {
        return participantService.findParticipant(eMail)
                .flatMap(certificateService::generateCertificate)
                .map(resource -> ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(resource))
                .get();
    }

}
