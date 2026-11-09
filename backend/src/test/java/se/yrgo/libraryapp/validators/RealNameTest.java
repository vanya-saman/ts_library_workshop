package se.yrgo.libraryapp.validators;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;

public class RealNameTest {

    @Test
    void loadBadWordsFile_doesNotThrowException() {
        assertThatNoException()
                .isThrownBy(RealName::loadBadWordsFile);
    }

    @Test
    void testReadBadWordsFileContainsAllBadWords() throws IOException {
        var badWords = RealName.loadBadWordsFile();
        assertThat(badWords.containsAll(List.of("malware", "worm", "null"))).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "malware",
            "worm",
            "null",
            "wonderful malware",
            "worm virus",
            "the malware destroys",
            "I-love-malware",
            "terrible;computer;worm",
    })
    void testValidatorRejectsBadWords(String badWord) {
        assertThat(RealName.validate(badWord)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "w0rm",
            "nu11",
            "m41w4re",
            "I 10v3 m4lw4r3"
    })
    void testValidatorRejectBadLeetWords(String badWord) {
        assertThat(RealName.validate(badWord)).isFalse();
    }
}
