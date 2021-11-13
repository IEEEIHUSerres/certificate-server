package org.ieeeihuserres.certificateserver.controller;

import java.net.URI;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class IndexController {
    private final CertificateServerConfig certificateServerConfig;

    @GetMapping("/")
    public ResponseEntity<Void> index() {
        log.info("Redirecting to " + certificateServerConfig.getEventUrl());
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(certificateServerConfig.getEventUrl()))
                .build();
    }
}
