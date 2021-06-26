package fr.esgi.pa.server.e2e;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.compiler.CodeState;
import fr.esgi.pa.server.code.infrastructure.entrypoint.TestCompileCodeRequest;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.common.core.utils.process.ProcessHelper;
import fr.esgi.pa.server.helper.AuthDataHelper;
import fr.esgi.pa.server.helper.AuthHelper;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.role.core.RoleDao;
import fr.esgi.pa.server.role.core.RoleName;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.util.Arrays;
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

    @Autowired
    private ProcessHelper processHelper;

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
                        "codeapitest",
                        "codeapitest@name.fr",
                        "codeapitest_password",
                        Set.of(presentUserRole));
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @AfterAll
    void afterAll() throws IOException, InterruptedException {
        var listLanguageExt = Arrays.asList("c", "java");

        for (String languageExt:  listLanguageExt) {
            var deleteContainerProcess = processHelper.launchCommandAndGetProcess(new String[]{"docker", "container", "rm", "code_container_" + languageExt});
            if (deleteContainerProcess.waitFor() != 0) {
                System.err.printf("Problem delete container 'code_container_%s'%n", languageExt);
            }
            var deleteImagesProcess = processHelper.launchCommandAndGetProcess(new String[]{"docker", "rmi", "code_image_" + languageExt});
            if (deleteImagesProcess.waitFor() != 0) {
                System.err.printf("Problem delete image 'code_image_%s'%n", languageExt);
            }
        }
    }

    @DisplayName("METHOD POST /api/code")
    @Nested
    class PostTestCompileCode {
        @Test
        void should_send_result_compiled_c_code() {
            var content = "#include <stdio.h>\n" +
                    "int main() {\n" +
                    "   // printf() displays the string inside quotation\n" +
                    "   printf(\"Hello World\");\n" +
                    "   return 0;\n" +
                    "}";
            var codeRequest = new TestCompileCodeRequest()
                    .setLanguage("C11")
                    .setContent(content);
            var code = given()
                    .header("Authorization", "Bearer " + authData.getToken())
                    .contentType(ContentType.JSON)
                    .body(codeRequest)
                    .when()
                    .post("/api/code/test")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(CodeResult.class);

            assertThat(code).isNotNull();
            assertThat(code.getLanguage()).isNotNull();
            assertThat(code.getCodeState()).isEqualTo(CodeState.SUCCESS);
            assertThat(code.getOutput()).isEqualTo("Hello World");
            assertThat(code.getLanguage().getLanguageName()).isEqualTo(LanguageName.C11);
            assertThat(code.getCodeState()).isEqualTo(CodeState.SUCCESS);
        }

        @Disabled
        @Test
        void should_send_result_compiled_java_code() {
            var content = "public class main {\n" +
                    "    public static void main(String[] args) {\n" +
                    "        System.out.println(\"Hello World\");\n" +
                    "    }\n" +
                    "}";
            var codeRequest = new TestCompileCodeRequest()
                    .setLanguage("JAVA8")
                    .setContent(content);
            var code = given()
                    .header("Authorization", "Bearer " + authData.getToken())
                    .contentType(ContentType.JSON)
                    .body(codeRequest)
                    .when()
                    .post("/api/code/test")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(CodeResult.class);

            assertThat(code).isNotNull();
            assertThat(code.getLanguage()).isNotNull();
            assertThat(code.getCodeState()).isEqualTo(CodeState.SUCCESS);
            assertThat(code.getOutput()).isEqualTo("Hello World\n");
            assertThat(code.getLanguage().getLanguageName()).isEqualTo(LanguageName.JAVA8);
            assertThat(code.getCodeState()).isEqualTo(CodeState.SUCCESS);
        }
    }
}
