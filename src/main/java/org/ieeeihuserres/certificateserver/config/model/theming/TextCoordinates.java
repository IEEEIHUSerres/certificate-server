package org.ieeeihuserres.certificateserver.config.model.theming;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "certificate.server.theming.text-coordinates")
@Getter
@Setter
public class TextCoordinates {
    private Integer x;
    private Integer y;
    private Integer rotation;
}
