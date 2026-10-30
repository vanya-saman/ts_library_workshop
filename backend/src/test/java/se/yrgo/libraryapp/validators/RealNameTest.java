package se.yrgo.libraryapp.validators;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RealNameTest {

    @Test
    void testReadBadWordsFileSucceeds() {
        try {
            assertThatNoException().isThrownBy(RealName::loadBadWordsFile);
        } catch (Exception e) {
        }
    }

}
