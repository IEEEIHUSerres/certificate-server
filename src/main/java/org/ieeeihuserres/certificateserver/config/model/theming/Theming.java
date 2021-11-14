package org.ieeeihuserres.certificateserver.config.model.theming;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "certificate.server.theming")
@Getter
@Setter
public class Theming {
    private Integer fontSize;
    private Color color;
    private TextCoordinates textCoordinates;
}
