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
    private final DefaultExerciseHelper defaultExerciseHelper;

    @Override
    @Transactional
    public Long createDefaultExercise(String title, String description, Language language, Long userId) {
        var savedExercise = saveExercise(title, description, userId);

        var defaultValues = defaultExerciseHelper.getValuesByLanguage(language);
        var savedCase = saveExerciseCase(language, savedExercise, defaultValues);

        saveExerciseTest(defaultValues, savedCase);
        return savedExercise.getId();
    }

    private void saveExerciseTest(DefaultExerciseValues defaultValues, JpaExerciseCase savedCase) {
        var testToSave = new JpaExerciseTest()
                .setExerciseCaseId(savedCase.getId())
                .setContent(defaultValues.getTestContent());
        exerciseTestRepository.save(testToSave);
    }

    private JpaExerciseCase saveExerciseCase(Language language, JpaExercise savedExercise, DefaultExerciseValues defaultValues) {
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

    private JpaExercise saveExercise(String title, String description, Long userId) {
        JpaExercise defaultExercise = new JpaExercise()
                .setTitle(title)
                .setDescription(description)
                .setUserId(userId);
        return exerciseRepository.save(defaultExercise);
    }
}
