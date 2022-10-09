package org.ieeeihuserres.certificateserver.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.ieeeihuserres.certificateserver.config.CertificateServerConfig;
import org.ieeeihuserres.certificateserver.config.model.theming.Theming;
import org.ieeeihuserres.certificateserver.model.Participant;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class CertificateService {
    private final CertificateServerConfig config;

    private static Try<? extends File> getFileFromFilePathString(String pdfTemplateFilePath) {
        return Try.of(() -> new File(pdfTemplateFilePath));
    }

    private static Try<Path> createTempCertificateFile() {
        return Try.of(() -> Files.createTempFile("certificate", ".pdf"));
    }

    public class Rotate extends PdfPageEventHelper {
        protected PdfNumber rotation = PdfPage.PORTRAIT;

        public void setRotation(PdfNumber rotation) {
            this.rotation = rotation;
        }

        public void onEndPage(PdfWriter writer, Document document) {
            writer.addPageDictEntry(PdfName.ROTATE, rotation);
        }
    }

    public Try<Resource> generateCertificate(Participant participant) {
        return Option.of(config.getCertificateTemplate())
                .toTry()
                .flatMap(CertificateService::getFileFromFilePathString)
                .flatMap(pdfTemplateFile -> CertificateService.createTempCertificateFile()
                        .flatMap(tempCertificateFile -> Try.of(() -> {
                            final InputStream inputStream = pdfTemplateFile.toURI().toURL().openStream();
                            final OutputStream outputStream = Files.newOutputStream(tempCertificateFile.toFile().toPath());

                            // Load existing PDF
                            PdfReader reader = new PdfReader(inputStream);

                            // Create output PDF
                            Document document = new Document(reader.getPageSize(1));
                            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
                            Rotate rotation = new Rotate();
                            writer.setPageEvent(rotation);
                            document.open();
                            PdfContentByte cb = writer.getDirectContent();

                            // Import existing PDF as template
                            PdfImportedPage page = writer.getImportedPage(reader, 1);

                            // Copy first page of existing PDF into output PDF
                            document.newPage();
                            cb.addTemplate(page, 0, 0);

                            // Add your new data / text here
                            addCertificateData(
                                    writer.getDirectContent(),
                                    String.format("%s %s", participant.getFirstName(), participant.getLastName()),
                                    config.getTheming()
                            );

                            document.close();

                            return tempCertificateFile;
                        })))
                .map(Path::toFile)
                .flatMap(tempCertificateFile -> Try.of(() -> new FileSystemResource(tempCertificateFile)));
    }

    public static BaseFont getDefaultBaseFont() {
        return Try.of(() -> BaseFont
                .createFont(
                        BaseFont.HELVETICA,
                        BaseFont.CP1252,
                        BaseFont.NOT_EMBEDDED)
        ).get();
    }

    public static void addCertificateData(final PdfContentByte cb,
                                          final String data,
                                          final Theming theming) {
        cb.saveState();
        cb.beginText();
        cb.setFontAndSize(getDefaultBaseFont(), theming.getFontSize());
        cb.setColorFill(new BaseColor(
                theming.getColor().getRed(),
                theming.getColor().getGreen(),
                theming.getColor().getBlue()
        ));
        cb.showTextAligned(
                PdfContentByte.ALIGN_CENTER,
                data,
                theming.getTextCoordinates().getX(),
                theming.getTextCoordinates().getY(),
                theming.getTextCoordinates().getRotation()
        );

        cb.endText();
        cb.restoreState();
    }

}
