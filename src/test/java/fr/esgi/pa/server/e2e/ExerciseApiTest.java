package fr.esgi.pa.server.e2e;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.util.DefaultExerciseHelper;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseCaseAdapter;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseTestAdapter;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.request.SaveExerciseRequest;
import fr.esgi.pa.server.helper.AuthDataHelper;
import fr.esgi.pa.server.helper.AuthHelper;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.language.core.exception.IncorrectLanguageNameException;
import fr.esgi.pa.server.role.core.RoleDao;
import fr.esgi.pa.server.role.core.RoleName;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Set;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
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

    @Autowired
    private DefaultExerciseHelper defaultExerciseHelper;

    @Autowired
    private ExerciseCaseDao exerciseCaseDao;

    @Autowired
    private ExerciseTestDao exerciseTestDao;

    @Autowired
    private ExerciseCaseAdapter exerciseCaseAdapter;

    @Autowired
    private ExerciseTestAdapter exerciseTestAdapter;

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
        void should_create_exercise_and_get_created_one() throws NotFoundException, IncorrectLanguageNameException {
            var foundLanguage = languageDao.findByLanguageName(LanguageName.JAVA);
            var javaDefaultValues = defaultExerciseHelper.getValuesByLanguage(foundLanguage);
            var exerciseRequest = new SaveExerciseRequest().setTitle("title exercise")
                    .setTitle("simple exercise")
                    .setDescription("return the string that is in parameter")
                    .setLanguage("JAVA");
            var postResponse = given()
                    .header("Authorization", "Bearer " + authData.getToken())
                    .contentType(ContentType.JSON)
                    .body(exerciseRequest)
                    .when()
                    .post("/api/exercise/")
                    .then()
                    .statusCode(201)
                    .extract()
                    .header("Location");
            assertThat(postResponse).isNotNull();
            assertThat(postResponse).contains("/api/exercise/");

            var getResponse = given()
                    .header("Authorization", "Bearer " + authData.getToken())
                    .when()
                    .get(postResponse)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(DtoExercise.class);
            assertThat(getResponse.getId()).isNotNull();
            assertThat(getResponse.getTitle()).isEqualTo(exerciseRequest.getTitle());
            assertThat(getResponse.getDescription()).isEqualTo(exerciseRequest.getDescription());
            assertThat(getResponse.getUserId()).isEqualTo(authData.getUser().getId());

            var foundExerciseCases = exerciseCaseDao.findAllByExerciseId(getResponse.getId());
            var javaLanguage = languageDao.findByStrLanguage(exerciseRequest.getLanguage());
            var expectedDtoCases = foundExerciseCases.stream()
                    .map(exerciseCase -> {
                        try {
                            assertThat(exerciseCase.getSolution()).isEqualTo(javaDefaultValues.getSolution());
                            assertThat(exerciseCase.getStartContent()).isEqualTo(javaDefaultValues.getStartContent());
                            var dtoSetExerciseTest = exerciseTestDao.findAllByExerciseCaseId(exerciseCase.getId())
                                    .stream()
                                    .map(exerciseTest -> {
                                        assertThat(exerciseTest.getContent()).isEqualTo(javaDefaultValues.getTestContent());
                                        return exerciseTestAdapter.domainToDto(exerciseTest);
                                    })
                                    .collect(Collectors.toSet());
                            return exerciseCaseAdapter.domainToDto(exerciseCase)
                                    .setTests(dtoSetExerciseTest)
                                    .setLanguage(javaLanguage);
                        } catch (NotFoundException e) {
                            fail("problem with findAllByExerciseCaseId : " + e.getMessage());
                        }
                        return null;
                    })
                    .collect(Collectors.toSet());
            assertThat(getResponse.getCases()).isEqualTo(expectedDtoCases);
        }
    }
}
