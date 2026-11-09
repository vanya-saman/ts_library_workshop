package se.yrgo.libraryapp.controllers;

import org.junit.jupiter.api.Test;
import se.yrgo.libraryapp.dao.RoleDao;
import se.yrgo.libraryapp.dao.SessionDao;
import se.yrgo.libraryapp.entities.Role;
import se.yrgo.libraryapp.entities.UserId;
import se.yrgo.libraryapp.services.UserService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class LoginControllerTest {


    @Test
    void isLoggedInWithoutCookieReturnsEmptyList(){
        var loginController = new LoginController(
                mock(UserService.class),
                mock(RoleDao.class),
                mock(SessionDao.class)
        );

        assertThat(loginController.isLoggedIn(null).isEmpty()).isTrue();
    }

    @Test
    void isLoggedInWithBadCookieReturnsEmptyList() {
        var loginController = new LoginController(
                mock(UserService.class),
                mock(RoleDao.class),
                mock(SessionDao.class)
        );

        assertThat(loginController.isLoggedIn("I-wish-I-was-a-UUID").isEmpty()).isTrue();
    }


    @Test
    void isLoggedInWithValidCookieReturnsRoles() {
        UUID cookie = UUID.randomUUID();
        List<Role> roles = List.of(Role.USER, Role.ADMIN);
        var userId = UserId.of(1);
        var sessionDao = mock(SessionDao.class);
        var roleDao = mock(RoleDao.class);

        when(sessionDao.validate(cookie)).thenReturn(userId);
        when(roleDao.get(userId)).thenReturn(roles);

        var loginController = new LoginController(
                mock(UserService.class),
                roleDao,
                sessionDao
        );

        assertThat(loginController.isLoggedIn(cookie.toString())).isEqualTo(roles);
    }


}
