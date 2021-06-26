package fr.esgi.pa.server.integration.exercise.infrastructure.dataprovider.util;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.utils.JpaDefaultExercise;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseTestRepository;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.role.core.RoleDao;
import fr.esgi.pa.server.role.core.RoleName;
import fr.esgi.pa.server.user.core.User;
import fr.esgi.pa.server.user.core.UserDao;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JpaDefaultExerciseTest {
    @Autowired
    private LanguageDao languageDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseCaseRepository exerciseCaseRepository;

    @Autowired
    private ExerciseTestRepository exerciseTestRepository;

    @Autowired
    private JpaDefaultExercise sut;

    private User admin;

    @BeforeAll
    void initAll() throws NotFoundException {
        var adminRole = roleDao.findByRoleName(RoleName.ROLE_ADMIN);
        if (adminRole.isPresent()) {
            var adminId = userDao.createUser("admin username", "admin@role.com", "adminpassword", Set.of(adminRole.get()));
            admin = userDao.findById(adminId);
        }
    }

    @AfterAll
    void tearDownAll() {
        if (admin != null) {
            userDao.deleteById(admin.getId());
        }
    }

    @Test
    void should_save_java_exercise_with_default_case_and_test() throws NotFoundException {
        var java = languageDao.findByLanguageName(LanguageName.JAVA);
        var testContent = "public class Main {\n" +
                "    public static void main(String[] args) throws Exception {\n" +
                "        String result = Solution.exercise1(\"toto\");\n" +
                "        if (result == null || !result.equals(\"toto\")) {\n" +
                "            throw new Exception(\"error expectations\");\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
        var startContent = "class Solution {\n" +
                "    public static String exercise1(String test) {\n" +
                "        // CODE HERE\n" +
                "        return null;\n" +
                "    }\n" +
                "}\n";
        var solution = "class Solution {\n" +
                "    public static String exercise1(String test) {\n" +
                "        // CODE HERE\n" +
                "        return test;\n" +
                "    }\n" +
                "}\n";

        var newExerciseId = sut.createDefaultExercise("title", "description", java, admin.getId());
        var maybeNewExercise = exerciseRepository.findById(newExerciseId);
        assertThat(maybeNewExercise.isPresent()).isTrue();
        var newExercise = maybeNewExercise.get();
        assertThat(newExercise.getId()).isEqualTo(newExerciseId);
        assertThat(newExercise.getUserId()).isEqualTo(admin.getId());
        assertThat(newExercise.getTitle()).isEqualTo("title");
        assertThat(newExercise.getDescription()).isEqualTo("description");

        var setCases = exerciseCaseRepository.findAllByExerciseId(newExerciseId);
        assertThat(setCases.size()).isEqualTo(1);
        var expectedCase = new ArrayList<>(setCases).get(0);
        assertThat(expectedCase.getId()).isNotNull();
        assertThat(expectedCase.getSolution()).isEqualTo(solution);
        assertThat(expectedCase.getStartContent()).isEqualTo(startContent);
        assertThat(expectedCase.getExerciseId()).isEqualTo(newExerciseId);
        assertThat(expectedCase.getIsValid()).isFalse();
        assertThat(expectedCase.getLanguageId()).isEqualTo(java.getId());

        var setTests = exerciseTestRepository.findAllByExerciseCaseId(expectedCase.getId());
        assertThat(setTests.size()).isEqualTo(1);
        var expectedTest = new ArrayList<>(setTests).get(0);
        assertThat(expectedTest).isNotNull();
        assertThat(expectedTest.getId()).isNotNull();
        assertThat(expectedTest.getContent()).isEqualTo(testContent);
        assertThat(expectedTest.getExerciseCaseId()).isEqualTo(expectedCase.getId());
    }
}