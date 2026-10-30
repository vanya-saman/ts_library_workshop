package se.yrgo.libraryapp.validators;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RealNameTest {

    @Test
    void testReadBadWordsFileSucceeds() {
        try {
            assertThatNoException().isThrownBy(RealName::loadBadWordsFile);
        } catch (Exception e) {
        }
    }

    @Test
    void testReadBadWordsFileContainsAllBadWords() {
        try {
            var badWords = RealName.loadBadWordsFile();
            assertThat(badWords.containsAll(List.of("malware", "worm", "null"))).isTrue();
        } catch (Exception e) {
        }
    }

}
