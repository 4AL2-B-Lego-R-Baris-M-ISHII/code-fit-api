package fr.esgi.pa.server.e2e;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.helper.AuthDataHelper;
import fr.esgi.pa.server.helper.AuthHelper;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.role.core.RoleDao;
import fr.esgi.pa.server.role.core.RoleName;
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
public class ExerciseApiTest {
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private LanguageDao languageDao;

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
        var adminRole = roleDao.findByRoleName(RoleName.ROLE_ADMIN);
        adminRole.ifPresent(presentAdminRole -> {
            try {
                authData = authHelper.createUserAndGetJwt(
                        "exerciseapi",
                        "exerciseapi@name.fr",
                        "exerciseapi_password",
                        Set.of(presentAdminRole));
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

    @Nested
    @DisplayName("POST /api/exercise")
    class PostOneExercise {
//        @Test
//        void should_create_exercise() throws NotFoundException {
//            var foundLanguage = languageDao.findByName(LanguageName.JAVA);
//            var test1 = new SaveTestExerciseRequest().setContent(
//                    "public class Main {\n" +
//                            "    public static void main(String[] args) {\n" +
//                            "        System.out.print(Solution.exercise1(\"toto\"));\n" +
//                            "    }\n" +
//                            "}"
//            ).setExpectedOutput("toto");
//            var test2 = new SaveTestExerciseRequest().setContent(
//                    "public class Main {\n" +
//                            "    public static void main(String[] args) {\n" +
//                            "        System.out.print(Solution.exercise1(\"Jacques Tati\"));\n" +
//                            "    }\n" +
//                            "}"
//            ).setExpectedOutput("Jacques Tati");
//            var listTestRequest = List.of(test1, test2);
//            var solutionText = "class Solution {\n" +
//                    "    public static String exercise1(String test) {\n" +
//                    "        return test;\n" +
//                    "    }\n" +
//                    "}";
//
//            var exerciseRequest = new SaveExerciseRequest().setTitle("title exercise")
//                    .setTitle("simple exercise")
//                    .setDescription("return the string that is in parameter")
//                    .setTests(listTestRequest)
//                    .setSolution(solutionText)
//                    .setLanguageId(foundLanguage.getId());
//            var response = given()
//                    .header("Authorization", "Bearer " + authData.getToken())
//                    .contentType(ContentType.JSON)
//                    .body(exerciseRequest)
//                    .when()
//                    .post("/api/exercise/")
//                    .then()
//                    .statusCode(201)
//                    .extract()
//                    .header("Location");
//            assertThat(response).isNotNull();
//            assertThat(response).contains("/api/exercise/");
//        }

        @Test
        void should_create_exercise() throws NotFoundException {
            var foundLanguage = languageDao.findByName(LanguageName.JAVA);

        }
    }
}
