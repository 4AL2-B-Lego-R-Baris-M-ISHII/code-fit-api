package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.utils;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise_case.core.utils.DefaultExerciseCase;
import fr.esgi.pa.server.exercise_case.core.utils.DefaultExerciseCaseHelper;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseCase;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseTestRepository;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.infrastructure.dataprovider.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaDefaultExerciseCase implements DefaultExerciseCase {
    private final ExerciseRepository exerciseRepository;
    private final LanguageRepository languageRepository;
    private final DefaultExerciseCaseHelper defaultExerciseCaseHelper;
    private final ExerciseCaseRepository exerciseCaseRepository;
    private final ExerciseTestRepository exerciseTestRepository;

    @Override
    public Long createExerciseCase(Long exerciseId, Language language) throws NotFoundException {
        checkIfExerciseExistsById(exerciseId);
        checkIfLanguageExistsById(language);

        var defaultValues = defaultExerciseCaseHelper.getValuesByLanguage(language);

        var savedExerciseCase = saveExerciseCaseAndTestWithDefaultValues(exerciseId, language, defaultValues);

        return savedExerciseCase.getId();
    }

    private JpaExerciseCase saveExerciseCaseAndTestWithDefaultValues(Long exerciseId, Language language, DefaultExerciseCaseValues defaultValues) {
        var exerciseCaseToSave = new JpaExerciseCase()
                .setIsValid(false)
                .setLanguageId(language.getId())
                .setExerciseId(exerciseId)
                .setStartContent(defaultValues.getStartContent())
                .setSolution(defaultValues.getSolution());
        var savedExerciseCase = exerciseCaseRepository.save(exerciseCaseToSave);
        var exerciseTestToSave = new JpaExerciseTest()
                .setExerciseCaseId(savedExerciseCase.getId())
                .setContent(defaultValues.getTestContent());
        exerciseTestRepository.save(exerciseTestToSave);
        return savedExerciseCase;
    }

    private void checkIfExerciseExistsById(Long exerciseId) throws NotFoundException {
        if (!exerciseRepository.existsById(exerciseId)) {
            var message = String.format(
                    "%s : Exercise with id '%d' not found",
                    CommonExceptionState.NOT_FOUND,
                    exerciseId
            );
            throw new NotFoundException(message);
        }
    }

    private void checkIfLanguageExistsById(Language language) throws NotFoundException {
        if (!languageRepository.existsById(language.getId())) {
            var message = String.format(
                    "%s : Language with id '%d' not found",
                    CommonExceptionState.NOT_FOUND,
                    language.getId()
            );
            throw new NotFoundException(message);
        }
    }
}
