package org.ieeeihuserres.certificateserver.repository;

import com.opencsv.CSVReader;
import io.vavr.Value;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.ieeeihuserres.certificateserver.config.CertificateServerConfig;
import org.ieeeihuserres.certificateserver.model.Participant;
import org.ieeeihuserres.certificateserver.util.GreekToLatinMapper;
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
                                csvRecords.flatMap(record -> Option.of(record[0]).toTry())
                                        .flatMap(record -> Try.of(() -> record.split(";")))
                                        .map(columns -> Try.of(() -> {
                                            final GreekToLatinMapper greekToLatinMapper = new GreekToLatinMapper();
                                            final String firstName = greekToLatinMapper.mapToLatin(columns[0]);
                                            final String lastName = greekToLatinMapper.mapToLatin(columns[1]);
                                            final String eMail = columns[2];
                                            return new Participant(firstName, lastName, eMail);
                                        }))
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
                .filter(participant -> participant.email().equals(email))
                .findFirst();
    }
}
