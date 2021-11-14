package org.ieeeihuserres.certificateserver.config.model.theming;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "certificate.server.theming.color")
@Getter
@Setter
public class Color {
    private Integer red;
    private Integer green;
    private Integer blue;
}
