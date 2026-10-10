package org.ieeeihuserres.certificateserver.repository;

import org.ieeeihuserres.certificateserver.config.CertificateServerConfig;
import org.ieeeihuserres.certificateserver.model.Participant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParticipantRepositoryTest {

    @TempDir
    Path tempDir;

    private static ParticipantRepository repositoryFor(String participantsFile) {
        final CertificateServerConfig config = new CertificateServerConfig();
        config.setParticipantsFile(participantsFile);
        return new ParticipantRepository(config);
    }

    private Path writeCsv(String content) throws IOException {
        final Path csv = tempDir.resolve("participants.csv");
        Files.writeString(csv, content, StandardCharsets.UTF_8);
        return csv;
    }

    @Test
    void loadsDefaultParticipantsFile() {
        final ParticipantRepository repository = repositoryFor("src/main/resources/default/participants.csv");

        assertThat(repository.findAll()).containsExactly(
                new Participant("Foteini", "Savvidou", "fsavvidou@i4h.org"),
                new Participant("Iordanis", "Kostelidis", "ikostelidis@i4h.org"),
                new Participant("Iordanis", "Kostelidis", "ikostelidis_gr@i4h.org"),
                new Participant("Chrysoula", "Tsimperi", "ctsimperi@i4h.org")
        );
    }

    @Test
    void skipsHeaderRow() throws IOException {
        final Path csv = writeCsv("firstName;lastName;eMail\nJohn;Doe;john@example.org\n");

        assertThat(repositoryFor(csv.toString()).findAll())
                .containsExactly(new Participant("John", "Doe", "john@example.org"));
    }

    @Test
    void transliteratesGreekNames() throws IOException {
        final Path csv = writeCsv("firstName;lastName;eMail\nΓιώργος;Παπαδάκης;gp@example.org\n");

        assertThat(repositoryFor(csv.toString()).findAll())
                .containsExactly(new Participant("Giorgos", "Papadakis", "gp@example.org"));
    }

    @Test
    void returnsEmptyListForHeaderOnlyFile() throws IOException {
        final Path csv = writeCsv("firstName;lastName;eMail\n");

        assertThat(repositoryFor(csv.toString()).findAll()).isEmpty();
    }

    @Test
    void findByEmailReturnsMatchingParticipant() throws IOException {
        final Path csv = writeCsv("firstName;lastName;eMail\nJohn;Doe;john@example.org\nJane;Roe;jane@example.org\n");

        assertThat(repositoryFor(csv.toString()).findByEmail("jane@example.org"))
                .contains(new Participant("Jane", "Roe", "jane@example.org"));
    }

    @Test
    void findByEmailIsCaseSensitive() throws IOException {
        final Path csv = writeCsv("firstName;lastName;eMail\nJohn;Doe;john@example.org\n");

        assertThat(repositoryFor(csv.toString()).findByEmail("JOHN@example.org")).isEmpty();
    }

    @Test
    void findByEmailReturnsEmptyForUnknownEmail() throws IOException {
        final Path csv = writeCsv("firstName;lastName;eMail\nJohn;Doe;john@example.org\n");

        assertThat(repositoryFor(csv.toString()).findByEmail("nobody@example.org")).isEmpty();
    }

    @Test
    void failsWhenParticipantsFileIsMissing() {
        final String missing = tempDir.resolve("missing.csv").toString();

        assertThatThrownBy(() -> repositoryFor(missing)).isInstanceOf(Exception.class);
    }

    @Test
    void failsWhenParticipantsFileIsNotConfigured() {
        assertThatThrownBy(() -> repositoryFor(null)).isInstanceOf(Exception.class);
    }

    @Test
    void failsOnRowWithMissingColumns() throws IOException {
        final Path csv = writeCsv("firstName;lastName;eMail\nJohn;Doe\n");

        assertThatThrownBy(() -> repositoryFor(csv.toString()))
                .isInstanceOf(ArrayIndexOutOfBoundsException.class);
    }
}
