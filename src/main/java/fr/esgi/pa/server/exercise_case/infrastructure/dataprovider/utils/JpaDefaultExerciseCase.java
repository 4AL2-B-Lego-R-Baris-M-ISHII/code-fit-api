package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.utils;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise_case.core.utils.DefaultExerciseCase;
import fr.esgi.pa.server.exercise_case.core.utils.DefaultExerciseCaseHelper;
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
    private final ExerciseCaseRepository exerciseCaseDao;

    @Override
    public Long createExerciseCase(Long exerciseId, Language language) throws NotFoundException {
        checkIfExerciseExistsById(exerciseId);
        checkIfLanguageExistsById(language);

        defaultExerciseCaseHelper.getValuesByLanguage(language);

        return null;
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
