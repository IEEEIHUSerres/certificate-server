package org.ieeeihuserres.certificateserver.repository;

import com.opencsv.CSVReader;
import io.vavr.Value;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.ieeeihuserres.certificateserver.config.CertificateServerConfig;
import org.ieeeihuserres.certificateserver.model.Participant;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Optional;

@Service
public class ParticipantRepository {
    private final CertificateServerConfig config;
    private final List<Participant> participants;

    public ParticipantRepository(CertificateServerConfig config) {
        this.config = config;
        this.participants = loadParticipants();
    }

    private List<Participant> loadParticipants() {
        return Option.of(config.getParticipantsFile())
                .toTry()
                .map(File::new)
                .flatMap(this::getFileReaderFromFile)
                .flatMap(this::getCSVReaderFromFileReader)
                .flatMap(this::getAllRecordsFromCSVReader)
                .flatMap(this::rejectFirstRecordBecauseItHasTitles)
                .map(io.vavr.collection.List::ofAll)
                .flatMap(csvRecords -> Try.sequence(
                                csvRecords.flatMap(strings -> Option.of(strings[0]).toTry())
                                        .flatMap(s -> Try.of(() -> s.split(";")))
                                        .map(strings -> Try.of(() -> new Participant(strings[0], strings[1], strings[2])))
                        )
                )
                .map(Value::toJavaList)
                .get();
    }

    private Try<List<String[]>> rejectFirstRecordBecauseItHasTitles(List<String[]> participantRecords) {
        return Try.of(() -> participantRecords.subList(1, participantRecords.size()));
    }

    private Try<List<String[]>> getAllRecordsFromCSVReader(CSVReader csvRecords) {
        return Try.of(csvRecords::readAll);
    }

    private Try<CSVReader> getCSVReaderFromFileReader(FileReader file) {
        return Try.of(() -> new CSVReader(file));
    }

    private Try<FileReader> getFileReaderFromFile(File file) {
        return Try.of(() -> new FileReader(file));
    }

    public List<Participant> findAll() {
        return participants;
    }

    public Optional<Participant> findByEmail(String email) {
        return participants.stream()
                .filter(participant -> participant.getEmail().equals(email))
                .findFirst();
    }
}
