package fr.esgi.pa.server.e2e;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.request.SaveExerciseRequest;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.request.UpdateExerciseRequest;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.utils.DefaultExerciseCaseHelper;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.utils.DefaultExerciseCaseValues;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseCaseAdapter;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseTestAdapter;
import fr.esgi.pa.server.helper.AuthDataHelper;
import fr.esgi.pa.server.helper.AuthHelper;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.language.core.exception.IncorrectLanguageNameException;
import fr.esgi.pa.server.role.core.RoleDao;
import fr.esgi.pa.server.role.core.RoleName;
import fr.esgi.pa.server.user.core.dao.UserDao;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
    private UserDao userDao;

    @Autowired
    private AuthHelper authHelper;

    @Autowired
    private DefaultExerciseCaseHelper defaultExerciseCaseHelper;

    @Autowired
    private ExerciseDao exerciseDao;

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

    @Test
    void should_crud_exercise() throws NotFoundException, IncorrectLanguageNameException {
        var foundLanguage = languageDao.findByLanguageName(LanguageName.JAVA8);
        var javaDefaultValues = defaultExerciseCaseHelper.getValuesByLanguage(foundLanguage);
        var exerciseRequest = new SaveExerciseRequest().setTitle("title exercise")
                .setTitle("simple exercise")
                .setDescription("return the string that is in parameter")
                .setLanguage("JAVA8");
        String uriOneExercise = postExerciseRequestAndAssertionsAndReturnURI(exerciseRequest);

        var dtoExercise = getOneExerciseRequestAndAssertions(javaDefaultValues, exerciseRequest, uriOneExercise);

        updateOneExerciseRequestAndAssertions(uriOneExercise, dtoExercise);

        given()
                .header("Authorization", "Bearer " + authData.getToken())
                .when()
                .delete(uriOneExercise)
                .then()
                .statusCode(204);

        dtoExercise.getCases().forEach(dtoExerciseCase -> {
            var tests = exerciseTestDao.findAllByExerciseCaseId(dtoExerciseCase.getId());
            assertThat(tests.size()).isEqualTo(0);
        });
        var cases = exerciseCaseDao.findAllByExerciseId(dtoExercise.getId());
        assertThat(cases.size()).isEqualTo(0);

        try {
            exerciseDao.findById(dtoExercise.getId());
            fail("findById should throw NotFoundException");
        } catch (NotFoundException ignored) {
        }
    }

    private String postExerciseRequestAndAssertionsAndReturnURI(SaveExerciseRequest exerciseRequest) {
        var uriOneExercise = given()
                .header("Authorization", "Bearer " + authData.getToken())
                .contentType(ContentType.JSON)
                .body(exerciseRequest)
                .when()
                .post("/api/exercise/")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");
        assertThat(uriOneExercise).isNotNull();
        assertThat(uriOneExercise).contains("/api/exercise/");
        return uriOneExercise;
    }

    private DtoExercise getOneExerciseRequestAndAssertions(
            DefaultExerciseCaseValues javaDefaultValues,
            SaveExerciseRequest exerciseRequest,
            String uriOneExercise) throws NotFoundException, IncorrectLanguageNameException {
        var getResponse = given()
                .header("Authorization", "Bearer " + authData.getToken())
                .when()
                .get(uriOneExercise)
                .then()
                .statusCode(200)
                .extract()
                .as(DtoExercise.class);
        assertThat(getResponse.getId()).isNotNull();
        assertThat(getResponse.getTitle()).isEqualTo(exerciseRequest.getTitle());
        assertThat(getResponse.getDescription()).isEqualTo(exerciseRequest.getDescription());
//        var dtoUser = new DtoUser().setId(authData.getUser().getId())
//                .setUsername(authData.getUser().getUsername())
//                .setEmail(authData.getUser().getEmail());
//        assertThat(getResponse.getUser()).isEqualTo(dtoUser);

        var foundExerciseCases = exerciseCaseDao.findAllByExerciseId(getResponse.getId());
        var javaLanguage = languageDao.findByStrLanguage(exerciseRequest.getLanguage());
        var expectedDtoCases = foundExerciseCases.stream()
                .map(exerciseCase -> {
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
                })
                .collect(Collectors.toSet());
        assertThat(getResponse.getCases()).isEqualTo(expectedDtoCases);
        return getResponse;
    }

    private void updateOneExerciseRequestAndAssertions(String uriOneExercise, DtoExercise dtoExercise) throws NotFoundException {
        var updateExerciseRequest = new UpdateExerciseRequest()
                .setTitle("update title")
                .setDescription("update description");
        given()
                .header("Authorization", "Bearer " + authData.getToken())
                .contentType(ContentType.JSON)
                .body(updateExerciseRequest)
                .when()
                .put(uriOneExercise)
                .then()
                .statusCode(204);
        var checkUpdateExercise = exerciseDao.findById(dtoExercise.getId());
        assertThat(checkUpdateExercise.getTitle()).isEqualTo(updateExerciseRequest.getTitle());
        assertThat(checkUpdateExercise.getDescription()).isEqualTo(updateExerciseRequest.getDescription());
    }
}
