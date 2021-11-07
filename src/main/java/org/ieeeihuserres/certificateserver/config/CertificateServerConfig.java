package org.ieeeihuserres.certificateserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties("certificate.server")
@Getter
@Setter
public class CertificateServerConfig {
    private String eventUrl;
    private String participantsFile;
}
