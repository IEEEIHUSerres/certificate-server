package org.ieeeihuserres.certificateserver.config;

import lombok.Getter;
import lombok.Setter;
import org.ieeeihuserres.certificateserver.config.model.theming.Theming;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("certificate.server")
@Getter
@Setter
public class CertificateServerConfig {
    private String eventUrl;
    private String participantsFile;
    private String certificateTemplate;
    private Theming theming;
}
