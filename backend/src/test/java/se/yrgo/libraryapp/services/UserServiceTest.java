package se.yrgo.libraryapp.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.yrgo.libraryapp.dao.UserDao;
import se.yrgo.libraryapp.entities.LoginInfo;
import se.yrgo.libraryapp.entities.UserId;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class UserServiceTest {
    @Mock
    private UserDao userDao;

    @Test
    @SuppressWarnings("deprecation")
    void correctLogin() {
        final String userId = "1";
        final UserId id = UserId.of(userId);
        final String username = "testuser";
        final String password = "password";
        final String passwordHash = "password";
        final LoginInfo info = new LoginInfo(id, passwordHash);
        final PasswordEncoder encoder =
                org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
        when(userDao.getLoginInfo(username)).thenReturn(Optional.of(info));
        UserService userService = new UserService(userDao, encoder);
        assertThat(userService.validate(username,
                password)).isEqualTo(Optional.of(id));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ab",
            "    da ",
            "      ",
            "ö"
    })
    @NullSource
    void isNameAvailableInvalidNames(String name){
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        UserService userService = new UserService(userDao, encoder);

        assertThat(userService.isNameAvailable(name)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Göran",
            "Mark",
            "Toole",
            "Steven",
            "April O' Neil"
    })
    void isNameAvailableValidNames(String name){
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        UserService userService = new UserService(userDao, encoder);
        when(userDao.isNameAvailable(name)).thenReturn(true);

        assertThat(userService.isNameAvailable(name)).isTrue();
    }

    @Test
    void registerSuccess() {
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        UserService userService = new UserService(userDao, encoder);

        when(encoder.encode("password")).thenReturn("hashed");
        when(userDao.register("testuser", "Bosse", "hashed")).thenReturn(true);

        assertThat(userService.register("testuser", "Bosse", "password")).isTrue();
    }
}