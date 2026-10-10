package org.ieeeihuserres.certificateserver.service;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import io.vavr.control.Try;
import org.ieeeihuserres.certificateserver.config.CertificateServerConfig;
import org.ieeeihuserres.certificateserver.config.model.theming.Color;
import org.ieeeihuserres.certificateserver.config.model.theming.TextCoordinates;
import org.ieeeihuserres.certificateserver.config.model.theming.Theming;
import org.ieeeihuserres.certificateserver.model.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class CertificateServiceTest {

    private static final String TEMPLATE = "src/main/resources/default/certificate.pdf";

    private CertificateServerConfig config;
    private CertificateService service;

    @BeforeEach
    void setUp() {
        final Color color = new Color();
        color.setRed(37);
        color.setGreen(24);
        color.setBlue(80);

        final TextCoordinates textCoordinates = new TextCoordinates();
        textCoordinates.setX(400);
        textCoordinates.setY(310);
        textCoordinates.setRotation(0);

        final Theming theming = new Theming();
        theming.setFontSize(40);
        theming.setColor(color);
        theming.setTextCoordinates(textCoordinates);

        config = new CertificateServerConfig();
        config.setCertificateTemplate(TEMPLATE);
        config.setTheming(theming);

        service = new CertificateService(config);
    }

    @Test
    void generatesSinglePagePdfContainingParticipantName() throws Exception {
        final Try<Resource> result = service.generateCertificate(new Participant("John", "Doe", "john@example.org"));

        assertThat(result.isSuccess()).isTrue();
        try (InputStream inputStream = result.get().getInputStream()) {
            final PdfReader reader = new PdfReader(inputStream);
            try {
                assertThat(reader.getNumberOfPages()).isEqualTo(1);
                assertThat(PdfTextExtractor.getTextFromPage(reader, 1)).contains("John Doe");
            } finally {
                reader.close();
            }
        }
    }

    @Test
    void keepsTemplatePageSize() throws Exception {
        final PdfReader template = new PdfReader(TEMPLATE);
        final Resource certificate = service.generateCertificate(new Participant("John", "Doe", "john@example.org")).get();

        try (InputStream inputStream = certificate.getInputStream()) {
            final PdfReader reader = new PdfReader(inputStream);
            try {
                assertThat(reader.getPageSize(1).getWidth()).isEqualTo(template.getPageSize(1).getWidth());
                assertThat(reader.getPageSize(1).getHeight()).isEqualTo(template.getPageSize(1).getHeight());
            } finally {
                reader.close();
                template.close();
            }
        }
    }

    @Test
    void writesEachCertificateToANewFile() throws Exception {
        final Resource first = service.generateCertificate(new Participant("John", "Doe", "john@example.org")).get();
        final Resource second = service.generateCertificate(new Participant("Jane", "Roe", "jane@example.org")).get();

        assertThat(first.getFile()).isNotEqualTo(second.getFile());
    }

    @Test
    void failsWhenTemplateIsNotConfigured() {
        config.setCertificateTemplate(null);

        assertThat(service.generateCertificate(new Participant("John", "Doe", "john@example.org")).isFailure()).isTrue();
    }

    @Test
    void failsWhenTemplateDoesNotExist() {
        config.setCertificateTemplate("does/not/exist.pdf");

        assertThat(service.generateCertificate(new Participant("John", "Doe", "john@example.org")).isFailure()).isTrue();
    }
}
