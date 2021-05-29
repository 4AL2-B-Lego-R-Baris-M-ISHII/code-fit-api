package fr.esgi.pa.server.unit.exercise.infrastructure.dataprovider.util;

import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExerciseTest;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseCaseMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseTestMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseTestRepository;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.util.JpaDefaultExercise;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class JpaDefaultExerciseTest {

    private JpaDefaultExercise sut;

    private final ExerciseTestMapper exerciseTestMapper = new ExerciseTestMapper();
    private final ExerciseCaseMapper exerciseCaseMapper = new ExerciseCaseMapper();
    private final ExerciseMapper exerciseMapper = new ExerciseMapper();

    @Mock
    private ExerciseRepository mockExerciseRepository;

    @Mock
    private ExerciseCaseRepository mockExerciseCaseRepository;

    @Mock
    private ExerciseTestRepository mockExerciseTestRepository;

    @BeforeEach
    void setup() {
        sut = new JpaDefaultExercise(mockExerciseRepository, mockExerciseCaseRepository, mockExerciseTestRepository);
    }

    @Test
    void should_call_repository_with_save_method() throws IOException {
        var totoTest = new JpaExerciseTest()
                .setContent("public class Main {\n" +
                        "    public static void main(String[] args) throws Exception {\n" +
                        "        var result = Solution.exercise1(\"toto\");\n" +
                        "        if (result == null || !result.equals(\"toto\")) {\n" +
                        "            throw new Exception(\"error expectations\");\n" +
                        "        }\n" +
                        "    }\n" +
                        "}");
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
        var language = new Language()
                .setId(1L)
                .setLanguageName(LanguageName.JAVA)
                .setFileExtension("java");
//        var exerciseCase = new JpaExerciseCase()
//                .setTests(List.of(totoTest))
//                .setLanguageId(1L)
//                .setIsValid(false)
//                .setStartContent(startContent)
//                .setLanguageId(language.getId())
//                .setSolution(solution);
//        var expectedExercise = new JpaExercise()
//                .setTitle("title")
//                .setDescription("description")
//                .setUserId(2L)
//                .setCases(Set.of(exerciseCase));
//
//        sut.createDefaultExercise("title", "description", language, 2L);
//
//        verify(mockExerciseRepository, times(1)).save(expectedExercise);
    }

    @Test
    void when_java_exercise_save_should_return_saved_one() throws IOException {
        var testToSave = new JpaExerciseTest()
                .setContent("public class Main {\n" +
                        "    public static void main(String[] args) throws Exception {\n" +
                        "        var result = Solution.exercise1(\"toto\");\n" +
                        "        if (result == null || !result.equals(\"toto\")) {\n" +
                        "            throw new Exception(\"error expectations\");\n" +
                        "        }\n" +
                        "    }\n" +
                        "}");
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
        var language = new Language()
                .setId(1L)
                .setLanguageName(LanguageName.JAVA)
                .setFileExtension("java");
//        var exerciseCaseToSave = new JpaExerciseCase()
//                .setTests(List.of(testToSave))
//                .setLanguageId(1L)
//                .setIsValid(false)
//                .setStartContent(startContent)
//                .setLanguageId(language.getId())
//                .setSolution(solution);
//        var exerciseToSave = new JpaExercise()
//                .setTitle("title")
//                .setDescription("description")
//                .setUserId(2L)
//                .setCases(Set.of(exerciseCaseToSave));
//
//        var savedToto = new JpaExerciseTest()
//                .setId(1L)
//                .setContent(testToSave.getContent());
//        var savedCase = new JpaExerciseCase()
//                .setId(1L)
//                .setIsValid(exerciseCaseToSave.getIsValid())
//                .setSolution(exerciseCaseToSave.getSolution())
//                .setStartContent(exerciseCaseToSave.getStartContent())
//                .setLanguageId(exerciseCaseToSave.getLanguageId())
//                .setTests(List.of(savedToto));
//        var savedExercise = new JpaExercise()
//                .setId(3L)
//                .setTitle(exerciseToSave.getTitle())
//                .setDescription(exerciseToSave.getDescription())
//                .setUserId(exerciseToSave.getUserId())
//                .setCases(Set.of(savedCase));
//
//        when(mockExerciseRepository.save(exerciseToSave)).thenReturn(savedExercise);
//
//        var result = sut.createDefaultExercise("title", "description", language, 2L);
//
//        var expectedTestExercise = exerciseTestMapper.entityToDomain(savedToto);
//        var expectedCaseExercise = exerciseCaseMapper.entityToDomain(savedCase)
//                .setTests(Set.of(expectedTestExercise));
//        var expectedExercise = exerciseMapper.entityToDomain(savedExercise)
//                .setCases(Set.of(expectedCaseExercise));
//
//        assertThat(expectedExercise).isNotNull();
//        assertThat(result).isEqualTo(expectedExercise);
    }

    // TODO : save default c exercise
}