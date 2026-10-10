package org.ieeeihuserres.certificateserver.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class GreekToLatinMapperTest {

    private final GreekToLatinMapper mapper = new GreekToLatinMapper();

    @ParameterizedTest
    @CsvSource({
            "Ιορδάνης, Iordanis",
            "Κωστελίδης, Kostelidis",
            "Θεόδωρος, Theodoros",
            "Χρυσάνθη, Chrysanthi",
            "Ψυχή, Psychi",
            "Αλέξανδρος, Alexandros",
            "Σοφία, Sofia",
            "Νίκος, Nikos",
    })
    void mapsGreekNamesToLatin(String greek, String latin) {
        assertThat(mapper.mapToLatin(greek)).isEqualTo(latin);
    }

    @Test
    void mapsFinalSigma() {
        assertThat(mapper.mapToLatin("ς")).isEqualTo("s");
    }

    @Test
    void mapsCharacterByCharacterWithoutDigraphRules() {
        assertThat(mapper.mapToLatin("Χρυσούλα")).isEqualTo("Chrysoyla");
    }

    @Test
    void keepsMultiLetterMappingsUpperCaseInUpperCaseWords() {
        assertThat(mapper.mapToLatin("ΘΕΟΔΩΡΟΣ ΧΡΥΣΑΝΘΗ")).isEqualTo("THEODOROS CHRYSANTHI");
        assertThat(mapper.mapToLatin("ΚΑΛΛΙΟΠΗ ΤΣΑΧ")).isEqualTo("KALLIOPI TSACH");
    }

    @Test
    void preservesCaseOfEachCharacter() {
        assertThat(mapper.mapToLatin("ΑΒΓ")).isEqualTo("AVG");
        assertThat(mapper.mapToLatin("αβγ")).isEqualTo("avg");
    }

    @Test
    void leavesLatinCharactersUnchanged() {
        assertThat(mapper.mapToLatin("Iordanis Kostelidis")).isEqualTo("Iordanis Kostelidis");
    }

    @Test
    void leavesUnmappedCharactersUnchanged() {
        assertThat(mapper.mapToLatin("Α-1 β.")).isEqualTo("A-1 v.");
    }

    @Test
    void returnsEmptyStringForEmptyInput() {
        assertThat(mapper.mapToLatin("")).isEmpty();
    }
}
