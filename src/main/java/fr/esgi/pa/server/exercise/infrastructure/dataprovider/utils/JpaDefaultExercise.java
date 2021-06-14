package fr.esgi.pa.server.exercise.infrastructure.dataprovider.utils;

import fr.esgi.pa.server.exercise.core.utils.DefaultExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise_case.core.utils.DefaultExerciseCaseHelper;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseCase;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseTestRepository;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.utils.DefaultExerciseCaseValues;
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
    private final DefaultExerciseCaseHelper defaultExerciseCaseHelper;

    @Override
    @Transactional
    public Long createDefaultExercise(String title, String description, Language language, Long userId) {
        var savedExercise = saveExercise(title, description, userId);

        var defaultValues = defaultExerciseCaseHelper.getValuesByLanguage(language);
        var savedCase = saveExerciseCase(language, savedExercise, defaultValues);

        saveExerciseTest(defaultValues, savedCase);
        return savedExercise.getId();
    }

    private JpaExercise saveExercise(String title, String description, Long userId) {
        JpaExercise defaultExercise = new JpaExercise()
                .setTitle(title)
                .setDescription(description)
                .setUserId(userId);
        return exerciseRepository.save(defaultExercise);
    }

    private JpaExerciseCase saveExerciseCase(Language language, JpaExercise savedExercise, DefaultExerciseCaseValues defaultValues) {
        var startContent = defaultValues.getStartContent();
        var solution = defaultValues.getSolution();
        var defaultCase = new JpaExerciseCase()
                .setExerciseId(savedExercise.getId())
                .setIsValid(false)
                .setStartContent(startContent)
                .setSolution(solution)
                .setLanguageId(language.getId());
        return exerciseCaseRepository.save(defaultCase);
    }

    private void saveExerciseTest(DefaultExerciseCaseValues defaultValues, JpaExerciseCase savedCase) {
        var testToSave = new JpaExerciseTest()
                .setExerciseCaseId(savedCase.getId())
                .setContent(defaultValues.getTestContent());
        exerciseTestRepository.save(testToSave);
    }
}
