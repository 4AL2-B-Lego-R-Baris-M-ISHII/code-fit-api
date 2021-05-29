package fr.esgi.pa.server.exercise.infrastructure.dataprovider.util;

import fr.esgi.pa.server.exercise.core.util.DefaultExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExerciseCase;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExerciseTest;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseTestRepository;
import fr.esgi.pa.server.language.core.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class JpaDefaultExercise implements DefaultExercise {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseCaseRepository exerciseCaseRepository;
    private final ExerciseTestRepository exerciseTestRepository;

    @Override
    @Transactional
    public Long createDefaultExercise(String title, String description, Language language, Long userId) {
        JpaExercise defaultExercise = new JpaExercise()
                .setTitle(title)
                .setDescription(description)
                .setUserId(userId);
        var savedExercise = exerciseRepository.save(defaultExercise);

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
        var defaultCase = new JpaExerciseCase()
                .setExerciseId(savedExercise.getId())
                .setIsValid(false)
                .setStartContent(startContent)
                .setSolution(solution)
                .setLanguageId(language.getId());
        var savedCase = exerciseCaseRepository.save(defaultCase);
        var testToSave = new JpaExerciseTest()
                .setExerciseCaseId(savedCase.getId())
                .setContent("public class Main {\n" +
                        "    public static void main(String[] args) throws Exception {\n" +
                        "        var result = Solution.exercise1(\"toto\");\n" +
                        "        if (result == null || !result.equals(\"toto\")) {\n" +
                        "            throw new Exception(\"error expectations\");\n" +
                        "        }\n" +
                        "    }\n" +
                        "}");
        exerciseTestRepository.save(testToSave);
        return savedExercise.getId();
    }
}
