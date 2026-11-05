package se.yrgo.libraryapp.validators;

import static org.assertj.core.api.Assertions.*;

import net.jqwik.api.ForAll;
import net.jqwik.api.constraints.AlphaChars;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import net.jqwik.api.Property;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.regex.Pattern;

public class UtilsTest {

    private static final Pattern LOWERCASE_EMPTY_AND_WHITESPACE = Pattern.compile("[a-z ]?+");

    @Property
    boolean lettersAndWhiteSpaceLowercase(@ForAll String str) {
        System.out.println("|"+str+"| -> |"+Utils.onlyLettersAndWhitespace(str)+"|");
        for (char c : Utils.onlyLettersAndWhitespace(str).toCharArray()){
            if(!Character.isWhitespace(c) && !Character.isLetter(c)){
                return false;
            }
        }
        return true;
    }

    @Test
    void testUnleetWonderful(){
        assertThat(Utils.cleanAndUnLeet("w0nd3rfu1")).isEqualTo("wonderful");
    }

    @Test
    void testUnleetLeet(){
        assertThat(Utils.cleanAndUnLeet("1337")).isEqualTo("leet");
    }

    @Test
    void testStringWithAllLeetNumbers(){
        assertThat(Utils.cleanAndUnLeet("01345678")).isEqualTo("oleasgtb");
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
