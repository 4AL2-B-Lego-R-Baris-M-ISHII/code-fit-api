package fr.esgi.pa.server.exercise_case.usecase;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseTest;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseCaseAdapter;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseTestAdapter;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.user.core.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetOneExerciseCase {
    private final ExerciseCaseDao exerciseCaseDao;
    private final UserDao userDao;
    private final ExerciseDao exerciseDao;
    private final LanguageDao languageDao;
    private final ExerciseTestDao exerciseTestDao;
    private final ExerciseCaseAdapter exerciseCaseAdapter;
    private final ExerciseTestAdapter exerciseTestAdapter;

    public DtoExerciseCase execute(Long userId, Long exerciseCaseId) throws NotFoundException, ForbiddenException {
        var foundExerciseCase = exerciseCaseDao.findById(exerciseCaseId);

        if (!foundExerciseCase.getIsValid()) {
            checkWhenCaseNotValidTheUserIsCreator(userId, foundExerciseCase);
        }

        return getDtoExerciseCase(foundExerciseCase);
    }

    private DtoExerciseCase getDtoExerciseCase(ExerciseCase foundExerciseCase) throws NotFoundException {
        var foundLanguage = languageDao.findById(foundExerciseCase.getLanguageId());
        var setTest = exerciseTestDao.findAllByExerciseCaseId(foundExerciseCase.getId());
        var dtoExercise = exerciseCaseAdapter.domainToDto(foundExerciseCase);

        dtoExercise.setLanguage(foundLanguage);
        dtoExercise.setTests(mapTestsToDtoExerciseTests(setTest));
        return dtoExercise;
    }

    private void checkWhenCaseNotValidTheUserIsCreator(Long userId, ExerciseCase foundExerciseCase) throws NotFoundException, ForbiddenException {
        checkIfUserExists(userId);
        checkIfUserIsCreatorOfExercise(userId, foundExerciseCase);
    }

    private void checkIfUserExists(Long userId) throws NotFoundException {
        if (!userDao.existsById(userId)) {
            var message = String.format(
                    "%s : User with id '%d' not found",
                    CommonExceptionState.NOT_FOUND,
                    userId
            );
            throw new NotFoundException(message);
        }
    }

    private void checkIfUserIsCreatorOfExercise(Long userId, ExerciseCase foundExerciseCase) throws NotFoundException, ForbiddenException {
        var foundExercise = exerciseDao.findById(foundExerciseCase.getExerciseId());
        if (!foundExercise.getUserId().equals(userId)) {
            var message = String.format(
                    "%s : When exercise case is not valid, only creator can get current exercise case",
                    CommonExceptionState.FORBIDDEN
            );
            throw new ForbiddenException(message);
        }
    }

    private Set<DtoExerciseTest> mapTestsToDtoExerciseTests(Set<ExerciseTest> setTest) {
        return setTest.stream()
                .map(exerciseTestAdapter::domainToDto)
                .collect(Collectors.toSet());
    }
}
