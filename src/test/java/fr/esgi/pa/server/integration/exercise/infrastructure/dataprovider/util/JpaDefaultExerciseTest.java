package fr.esgi.pa.server.integration.exercise.infrastructure.dataprovider.util;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.entity.ExerciseTest;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseCaseMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseTestMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.util.JpaDefaultExercise;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.role.core.RoleDao;
import fr.esgi.pa.server.role.core.RoleName;
import fr.esgi.pa.server.user.core.User;
import fr.esgi.pa.server.user.core.UserDao;
import org.junit.jupiter.api.*;
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
    private ExerciseTestMapper exerciseTestMapper;

    @Autowired
    private ExerciseCaseMapper exerciseCaseMapper;

    @Autowired
    private ExerciseMapper exerciseMapper;

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

    @BeforeEach
    void setup() {
        sut = new JpaDefaultExercise(exerciseRepository, exerciseCaseRepository, exerciseTestMapper, exerciseCaseMapper, exerciseMapper);
    }

    @Test
    void should_save_java_exercise_with_default_case_and_test() throws NotFoundException {
        var java = languageDao.findByLanguageName(LanguageName.JAVA);
        var testContent = "public class Main {\n" +
                "    public static void main(String[] args) throws Exception {\n" +
                "        var result = Solution.exercise1(\"toto\");\n" +
                "        if (result == null || !result.equals(\"toto\")) {\n" +
                "            throw new Exception(\"error expectations\");\n" +
                "        }\n" +
                "    }\n" +
                "}";
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

        var result = sut.createDefaultExercise("title", "description", java, admin.getId());

        assertThat(result).isNotNull();
        var createdExercise = exerciseRepository.findById(result.getId())
                .orElse(null);
        assertThat(createdExercise).isNotNull();
        assertThat(createdExercise.getId()).isNotNull();
        assertThat(createdExercise.getTitle()).isEqualTo("title");
        assertThat(createdExercise.getDescription()).isEqualTo("description");
        assertThat(createdExercise.getUserId()).isEqualTo(admin.getId());
        assertThat(createdExercise.getCases()).isNotNull();
        assertThat(createdExercise.getCases().size()).isEqualTo(1);

        var firstCase = new ArrayList<>(createdExercise.getCases()).get(0);
        assertThat(firstCase).isNotNull();
        assertThat(firstCase.getId()).isNotNull();
        assertThat(firstCase.getStartContent()).isEqualTo(startContent);
        assertThat(firstCase.getSolution()).isEqualTo(solution);
        assertThat(firstCase.getTests()).isNotNull();
        assertThat(firstCase.getTests().size()).isEqualTo(1);

        var firstTest = new ArrayList<>(firstCase.getTests()).get(0);
        assertThat(firstTest).isNotNull();
        assertThat(firstTest.getId()).isNotNull();
        assertThat(firstTest.getContent()).isEqualTo(testContent);
    }
}