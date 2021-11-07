package org.ieeeihuserres.certificateserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    @GetMapping("/generate/{eMail}")
    public ResponseEntity<String> generateCertificate(@PathVariable("eMail") String eMail) {
        return ResponseEntity.ok("Certificate generated for " + eMail);
    }
    
}
