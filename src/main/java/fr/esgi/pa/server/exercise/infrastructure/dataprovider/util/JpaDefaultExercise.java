package fr.esgi.pa.server.exercise.infrastructure.dataprovider.util;

import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.core.util.DefaultExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExerciseCase;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseCaseMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseTestMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.language.core.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaDefaultExercise implements DefaultExercise {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseCaseRepository exerciseCaseRepository;
    private final ExerciseTestMapper exerciseTestMapper;
    private final ExerciseCaseMapper exerciseCaseMapper;
    private final ExerciseMapper exerciseMapper;

    @Override
    @Transactional
    public Exercise createDefaultExercise(String title, String description, Language language, Long userId) {
        JpaExercise defaultExercise = new JpaExercise()
                .setTitle(title)
                .setDescription(description)
                .setUserId(userId);
        var savedExercise = exerciseRepository.save(defaultExercise);
        System.out.println("-------------- saved exercise ------------");
        System.out.println(savedExercise);

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
                .setExercise(savedExercise)
                .setIsValid(false)
                .setStartContent(startContent)
                .setSolution(solution)
                .setLanguageId(language.getId());
        var savedCase = exerciseCaseRepository.save(defaultCase);
        System.out.println("----------- exercise case --------------");
        System.out.println(savedCase);
//        var result = mapJpaToDomainExerciseWithSubProperties(savedExercise);
//        System.out.println("---------------result-----------");
//        System.out.println(result);

//        var caseResult = exerciseCaseRepository.findByExercise(savedExercise);
//        if (caseResult.isPresent()) {
//            System.out.println(caseResult.get());
//        } else {
//            System.out.println("Not found");
//        }
        var toto = exerciseRepository.findById(savedExercise.getId());
        return toto.map(this::mapJpaToDomainExerciseWithSubProperties).orElse(null);
    }

//    private JpaExercise prepareDefaultExercise(String title, String description, Language language, Long userId) {
//        var defaultTest = new JpaExerciseTest()
//                .setContent("public class Main {\n" +
//                        "    public static void main(String[] args) throws Exception {\n" +
//                        "        var result = Solution.exercise1(\"toto\");\n" +
//                        "        if (result == null || !result.equals(\"toto\")) {\n" +
//                        "            throw new Exception(\"error expectations\");\n" +
//                        "        }\n" +
//                        "    }\n" +
//                        "}");
//        var defaultCase = new JpaExerciseCase()
//                .setTests(Set.of(defaultTest))
//                .setLanguageId(language.getId())
//                .setIsValid(false)
//                .setStartContent("class Solution {\n" +
//                        "    public static String exercise1(String test) {\n" +
//                        "        // CODE HERE\n" +
//                        "        return null;\n" +
//                        "    }\n" +
//                        "}\n")
//                .setSolution("class Solution {\n" +
//                        "    public static String exercise1(String test) {\n" +
//                        "        // CODE HERE\n" +
//                        "        return test;\n" +
//                        "    }\n" +
//                        "}\n");
//        return new JpaExercise()
//                .setTitle(title)
//                .setDescription(description)
//                .setUserId(userId)
//                .setCases(Set.of(defaultCase));
//    }

    private Exercise mapJpaToDomainExerciseWithSubProperties(JpaExercise savedExercise) {
        var setCases = savedExercise.getCases()
                .stream()
                .map(currentCase -> {
                    var setTests = currentCase.getTests().stream()
                            .map(exerciseTestMapper::entityToDomain)
                            .collect(Collectors.toSet());
                    return exerciseCaseMapper.entityToDomain(currentCase)
                            .setTests(setTests);
                })
                .collect(Collectors.toSet());
        return exerciseMapper.entityToDomain(savedExercise)
                .setCases(setCases);
    }
}
