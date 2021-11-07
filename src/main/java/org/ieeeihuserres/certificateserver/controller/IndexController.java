package org.ieeeihuserres.certificateserver.controller;

import java.net.URI;

import org.ieeeihuserres.certificateserver.config.CertificateServerConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexController {
    private final CertificateServerConfig certificateServerConfig;

    @GetMapping("/")
    public ResponseEntity<Void> index() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(certificateServerConfig.getEventUrl()))
                .build();
    }
}
