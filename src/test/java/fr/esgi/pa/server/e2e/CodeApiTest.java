package fr.esgi.pa.server.e2e;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.infrastructure.entrypoint.CodeRequest;
import fr.esgi.pa.server.common.exception.NotFoundException;
import fr.esgi.pa.server.helper.AuthHelper;
import fr.esgi.pa.server.role.core.RoleDao;
import fr.esgi.pa.server.role.core.RoleName;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
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
public class CodeApiTest {
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private AuthHelper authHelper;

    @LocalServerPort
    private int localPort;

    private String jwtUser;

    @BeforeEach
    void setup() {
        port = localPort;
    }

    @BeforeAll
    void initAll() {
        var userRole = roleDao.findByRoleName(RoleName.ROLE_USER);
        userRole.ifPresent(presentUserRole -> {
            try {
                jwtUser = authHelper.createUserAndGetJwt(
                        "user",
                        "user@name.fr",
                        "password",
                        Set.of(presentUserRole));
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @DisplayName("METHOD POST /api/code")
    @Nested
    class PostCompileCode {
        @Test
        void should_send_result_compiled_code() {
            var content = "#include <stdio.h>\n" +
                    "int main() {\n" +
                    "   // printf() displays the string inside quotation\n" +
                    "   printf(\"Hello World\")\n" +
                    "   return 0;\n" +
                    "}";
            var codeRequest = new CodeRequest()
                    .setLanguage("C")
                    .setContent(content);
            given()
                    .header("Authorization", "Bearer " + jwtUser)
                    .contentType(ContentType.JSON)
                    .body(codeRequest)
                    .when()
                    .post("/api/code")
                    .then()
                    .statusCode(200);

            // TODO : assert language status, name and content
        }
    }
}
