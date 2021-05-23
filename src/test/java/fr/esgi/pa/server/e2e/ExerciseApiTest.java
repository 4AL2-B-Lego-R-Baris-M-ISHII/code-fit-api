package fr.esgi.pa.server.e2e;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.helper.AuthDataHelper;
import fr.esgi.pa.server.helper.AuthHelper;
import fr.esgi.pa.server.role.core.RoleDao;
import fr.esgi.pa.server.role.core.RoleName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExerciseApiTest {
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private AuthHelper authHelper;

    @LocalServerPort
    private int localPort;

    private AuthDataHelper authData;

    @BeforeEach
    void setup() {
        port = localPort;
    }

    @BeforeAll
    void initAll() {
        var userRole = roleDao.findByRoleName(RoleName.ROLE_USER);
        userRole.ifPresent(presentUserRole -> {
            try {
                authData = authHelper.createUserAndGetJwt(
                        "exerciseapi",
                        "exerciseapi@name.fr",
                        "exerciseapi_password",
                        Set.of(presentUserRole));
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void get_test() {
        var response = given()
                .header("Authorization", "Bearer " + authData.getToken())
                .when()
                .get("/api/exercise/test")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        assertThat(response).isEqualTo("userId : " + authData.getUser().getId());
    }
}
