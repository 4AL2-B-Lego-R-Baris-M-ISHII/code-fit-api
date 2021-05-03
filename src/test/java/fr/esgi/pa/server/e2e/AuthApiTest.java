package fr.esgi.pa.server.e2e;

import fr.esgi.pa.server.user.core.UserDao;
import fr.esgi.pa.server.auth.infrastructure.entrypoint.LoginRequest;
import fr.esgi.pa.server.auth.infrastructure.entrypoint.SignUpRequest;
import fr.esgi.pa.server.auth.infrastructure.entrypoint.JwtResponse;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthApiTest {
    @LocalServerPort
    private int localPort;

    @Autowired
    private UserDao userDao;

    @BeforeEach
    void setup() {
        port = localPort;
    }

    @DisplayName("METHOD POST /api/auth/signup")
    @Nested
    class SignUp {
        @Test
        void when_request_is_correct_should_send_success_response() {
            var signUpRequest = new SignUpRequest()
                    .setUsername("new user name")
                    .setEmail("newuser@email.com")
                    .setPassword("newuserpassword");
            given()
                    .contentType(ContentType.JSON)
                    .body(signUpRequest)
                    .when()
                    .post("/api/auth/signup")
                    .then()
                    .statusCode(201);
            assertThat(userDao.existsByUsername(signUpRequest.getUsername())).isTrue();
            assertThat(userDao.existsByEmail(signUpRequest.getEmail())).isTrue();
        }
    }

    @DisplayName("METHOD POST /api/auth/signin")
    @Nested
    class SignIn {
        @Test
        void when_user_already_sign_up_send_sign_in_request_should_send_jwt_response() {
            var signUpRequest = new SignUpRequest()
                    .setUsername("another user name")
                    .setEmail("anotheruser@email.com")
                    .setPassword("anotheruserpassword");
            given()
                    .contentType(ContentType.JSON)
                    .body(signUpRequest)
                    .when()
                    .post("/api/auth/signup")
                    .then()
                    .statusCode(201);

            var loginRequest = new LoginRequest()
                    .setUsername(signUpRequest.getUsername())
                    .setPassword(signUpRequest.getPassword());
            var expectedJwt = new JwtResponse()
                    .setUsername(signUpRequest.getUsername())
                    .setEmail(signUpRequest.getEmail())
                    .setRoles(Collections.singletonList("ROLE_USER"));

            var jwtResponse = given()
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when()
                    .post("/api/auth/signin")
                    .then()
                    .statusCode(201)
                    .extract()
                    .as(JwtResponse.class);
            assertThat(jwtResponse.getId()).isNotNull();
            assertThat(jwtResponse.getToken()).isNotNull();
            assertThat(jwtResponse.getRoles()).isEqualTo(expectedJwt.getRoles());
            assertThat(jwtResponse.getUsername()).isEqualTo(expectedJwt.getUsername());
            assertThat(jwtResponse.getEmail()).isEqualTo(expectedJwt.getEmail());
        }
    }
}
