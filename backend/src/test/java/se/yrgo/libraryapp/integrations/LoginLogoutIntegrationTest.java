package se.yrgo.libraryapp.integrations;

import io.jooby.JoobyTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.yrgo.libraryapp.App;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("foobar")
@Testcontainers
@JoobyTest(value = App.class, factoryMethod = "createApp")
public class LoginLogoutIntegrationTest {
    @Container
    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.29");

    public static App createApp() {
        System.setProperty("db.url", mySQLContainer.getJdbcUrl());
        System.setProperty("db.user", mySQLContainer.getUsername());
        System.setProperty("db.password", mySQLContainer.getPassword());
        return new App();
    }

    public static HttpResponse<String> makeRequest(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> makePost(String url, String body) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers
                        .ofString(body))
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    @AfterAll
    static void reset() {
        System.clearProperty("db.url");
        System.clearProperty("db.user");
        System.clearProperty("db.password");
    }


    @Test
    public void alwaysOkToLogout(int serverPort) {
        try {
            var resp = makeRequest("http://localhost:" + serverPort + "/logout");
            assertEquals("", resp.body());
            assertEquals(HttpURLConnection.HTTP_OK, resp.statusCode());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void cantLogInWithNonExistingUser(int serverPort) throws IOException, InterruptedException {
        final String base = "http://localhost:" + serverPort;
        HttpResponse<String> loginResp = makePost(
                base + "/login",
                "{\"username\":\"frogman\",\"password\":\"passMasSE3\"}"
        );
        assertThat(loginResp.statusCode()).isNotEqualTo(HttpURLConnection.HTTP_OK);
    }

    @Test
    public void canLogInAndOutWithoutError(int serverPort) throws Exception {
        final String base = "http://localhost:" + serverPort;
        final HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> loginResp = makePost(
                base + "/login",
                "{\"username\":\"test\",\"password\":\"yrgoP4ssword\"}"
        );

        assertThat(loginResp.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);

        String setCookie = loginResp.headers().firstValue("Set-Cookie").orElse("");
        assertThat(setCookie).startsWith("session=");
        String sessionCookie = setCookie.split(";")[0];

        HttpRequest checkReq = HttpRequest.newBuilder(URI.create(base + "/login/check"))
                .header("Cookie", sessionCookie)
                .build();
        HttpResponse<String> checkResp = client.send(checkReq, HttpResponse.BodyHandlers.ofString());

        assertThat(checkResp.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(checkResp.body()).isNotEqualTo("[]");

        HttpRequest logoutReq = HttpRequest.newBuilder(URI.create(base + "/logout"))
                .header("Cookie", sessionCookie)
                .build();
        HttpResponse<String> logoutResp = client.send(logoutReq, HttpResponse.BodyHandlers.ofString());

        assertThat(logoutResp.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(logoutResp.body()).isEmpty();

        HttpResponse<String> afterResp = client.send(checkReq, HttpResponse.BodyHandlers.ofString());
        assertThat(afterResp.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(afterResp.body()).isEqualTo("[]");
    }

    @Test
    public void canRegisterAndLogin(int serverPort) throws IOException, InterruptedException {
        String base = "http://localhost:" + serverPort;

        var registerResp = makePost(base + "/register",
                """
                        {
                            "name": "froogy",
                            "realName": "Frog mcFrogster",
                            "password": "secret"
                        }
                        """);

        assertThat(registerResp.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);

        HttpResponse<String> loginResp = makePost(base + "/login",
                """
                        {
                            "username": "froogy",
                            "password": "secret"
                        }
                        """
        );
        assertThat(loginResp.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
    }

    @Test
    public void cantRegisterWithNullName(int serverPort) throws IOException, InterruptedException {
        final String base = "http://localhost:" + serverPort;
        var registerResp = makePost(base + "/register",
                """
                        {
                            "name": null,
                            "realName": "Frog mcFrogster",
                            "password": "secret"
                        }
                        """);
        assertThat(registerResp.statusCode()).isNotEqualTo(HttpURLConnection.HTTP_OK);
    }

    @Test
    public void cantRegisterWithShortName(int serverPort) throws IOException, InterruptedException {
        final String base = "http://localhost:" + serverPort;
        var registerResp = makePost(base + "/register",
                """
                        {
                            "name": "sh",
                            "realName": "Frog mcFrogster",
                            "password": "secret"
                        }
                        """);
        assertThat(registerResp.body()).isEqualTo("false");

    }

}

