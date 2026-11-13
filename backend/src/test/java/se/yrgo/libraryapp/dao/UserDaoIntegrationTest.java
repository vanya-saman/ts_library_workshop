package se.yrgo.libraryapp.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.radcortez.flyway.test.annotation.H2;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.yrgo.libraryapp.entities.User;
import se.yrgo.libraryapp.entities.UserId;

@Tag("integration")
@Testcontainers
public class UserDaoIntegrationTest {

    @Container
    private static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.29");
    private static DataSource ds;

    @BeforeAll
    static void setup() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(mysql.getJdbcUrl());
        hikariConfig.setUsername(mysql.getUsername());
        hikariConfig.setPassword(mysql.getPassword());
        hikariConfig.setDriverClassName(mysql.getDriverClassName());
        UserDaoIntegrationTest.ds = new HikariDataSource(hikariConfig);
        Flyway flyway = Flyway.configure().dataSource(ds).load();
        flyway.migrate();
    }


    @Test
    void getNonExistingUserById() {
        UserDao userDao = new UserDao(ds);
        assertThat(userDao.get("9999")).isEmpty();
    }

    @Test
    void getUserByLettersId() {
        UserDao userDao = new UserDao(ds);
        assertThat(userDao.get("ASDCS")).isEmpty();
    }

    @Test
    void nameIsNoLongerAvailableAfterRegistering() {
        UserDao userDao = new UserDao(ds);
        assertThat(userDao.isNameAvailable("newuser")).isTrue();
        userDao.register("newuser", "New User", "hash123");
        assertThat(userDao.isNameAvailable("newuser")).isFalse();
    }


}