package org.ieeeihuserres.certificateserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ieeeihuserres.certificateserver.config.CertificateServerConfig;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Arrays;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class IndexController {
    private final CertificateServerConfig certificateServerConfig;
    private final Environment environment;

    @GetMapping("/")
    public ResponseEntity<Void> index() {
        log.info("Active environments " + Arrays.stream(environment.getActiveProfiles()).reduce((a, b) -> a + ", " + b));
        log.info("Redirecting to " + certificateServerConfig.getEventUrl());
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(certificateServerConfig.getEventUrl()))
                .build();
    }
}
