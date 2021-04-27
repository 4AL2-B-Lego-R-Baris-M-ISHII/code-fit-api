package fr.esgi.pa.server.e2e;

import fr.esgi.pa.server.core.dao.RoleDao;
import fr.esgi.pa.server.core.dao.UserDao;
import fr.esgi.pa.server.infrastructure.entrypoint.request.LoginRequest;
import fr.esgi.pa.server.infrastructure.entrypoint.request.SignUpRequest;
import fr.esgi.pa.server.infrastructure.entrypoint.response.JwtResponse;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthApiTest {
    @LocalServerPort
    private int localPort;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

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
                    .statusCode(200);
        }
    }

    @DisplayName("METHOD POST /api/auth/signin")
    @Nested
    class SignIn {
        @Test
        void when_user_already_sign_up_send_sign_in_request_should_send_jwt_response() {
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
                    .statusCode(200);

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
                    .statusCode(200)
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
