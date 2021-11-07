package org.ieeeihuserres.certificateserver;

import org.ieeeihuserres.certificateserver.config.CertificateServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CertificateServerConfig.class)
public class CertificateServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CertificateServerApplication.class, args);
    }

}
