package se.yrgo.libraryapp.validators;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsernameTest {

    @ParameterizedTest
    @ValueSource(strings={
            "username",
            "bosse",
            "CaptializedName",
            "-hello@",
            "@.-0"
    })
    void correctUsername() {
        assertThat(Username.validate("bosse")).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings={
            "gg",
            "user name with space",
            "one space",
            "emoji☺️",
            "/admin",
            "rm -rf /",
            "9523",
            "ascfeaef€¥3",
            "avf_45",
            "日本語おｋですか",
            "hello\n"
    })
    @EmptySource
    void incorrectUsername(String username) {
        assertThat(Username.validate(username)).isFalse();
    }

    @Test
    void incorrectNullUsername(){
        assertThat(Username.validate(null)).isFalse();
    }
}
