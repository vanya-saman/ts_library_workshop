package se.yrgo.libraryapp.validators;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;

public class UtilsTest {

    private static final Pattern LOWERCASE_EMPTY_AND_WHITESPACE = Pattern.compile("[a-z ]+|");

    @ParameterizedTest
    @ValueSource(strings = {
            "ALLCAPS",
            "someCaps",
            "endcaP",
            "Startcap",
            "CaPs And Spcaes",
            "Some@Special¥Stuff",
            "£$€{¥€{",
            "hello@mail.com",
            "££hello"
    })
    @EmptySource
    void testLettersAndWhiteSpaceLowercase(String str) {
        assertThat(
                LOWERCASE_EMPTY_AND_WHITESPACE.matcher(Utils.onlyLettersAndWhitespace(str)).matches()
        ).isTrue();
    }


    @Test
    void testUnleetWonderful(){
        assertThat(Utils.cleanAndUnLeet("w0nd3rfu1")).isEqualTo("wonderful");
    }

    @Test
    void testUnleetLeet(){
        assertThat(Utils.cleanAndUnLeet("1337")).isEqualTo("leet");
    }

    @ParameterizedTest
    @ValueSource(strings={
            "1337",
            "w0nd5rfu1",
            "Xx5N1P3RxX",
            "Gr34tV3g3tables"
    })
    void testLeetNumbersAreGone(String str){
        assertThat(Utils.cleanAndUnLeet(str).matches("[01345678]")).isFalse();
    }


}
