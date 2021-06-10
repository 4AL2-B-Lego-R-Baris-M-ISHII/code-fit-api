package fr.esgi.pa.server.exercise_case.usecase;

import fr.esgi.pa.server.common.core.exception.AlreadyCreatedException;
import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreateExerciseCase {
    private final UserDao userDao;
    private final ExerciseDao exerciseDao;
    private final ExerciseCaseDao exerciseCaseDao;
    private final LanguageDao languageDao;

    public Long execute(long userId, long exerciseId, long languageId) throws NotFoundException, ForbiddenException, AlreadyCreatedException {
        var foundExercise = exerciseDao.findById(exerciseId);
        checkIfUserExists(userId);
        checkIfUserIsCreatorOfExercise(userId, foundExercise);

        var foundLanguage = languageDao.findById(languageId);
        var setExerciseCase = exerciseCaseDao.findAllByExerciseId(exerciseId);

        checkIfSetExerciseCaseContainFoundLanguage(foundLanguage, setExerciseCase);

        return null;
    }

    private void checkIfUserExists(long userId) throws NotFoundException {
        if (!userDao.existsById(userId)) {
            var message = String.format("%s : User with id '%d' not exists", CommonExceptionState.NOT_FOUND, userId);
            throw new NotFoundException(message);
        }
    }

    private void checkIfUserIsCreatorOfExercise(long userId, fr.esgi.pa.server.exercise.core.entity.Exercise foundExercise) throws ForbiddenException {
        if (foundExercise.getUserId() != userId) {
            var message = String.format(
                    "%s : Exercise case has to create by creator of concerned exercise",
                    CommonExceptionState.FORBIDDEN
            );
            throw new ForbiddenException(message);
        }
    }

    private void checkIfSetExerciseCaseContainFoundLanguage(Language foundLanguage, Set<ExerciseCase> setExerciseCase) throws AlreadyCreatedException {
        if (isExerciseCaseWithFoundLanguage(foundLanguage, setExerciseCase)) {
            var message = String.format(
                    "%s : Exercise case with language '%s' already created",
                    CommonExceptionState.ALREADY_CREATED,
                    foundLanguage.getLanguageName()
            );
            throw new AlreadyCreatedException(message);
        }
    }

    private boolean isExerciseCaseWithFoundLanguage(Language foundLanguage, Set<ExerciseCase> setExerciseCase) {
        return setExerciseCase
                .stream()
                .anyMatch(exerciseCase -> exerciseCase.getLanguageId()
                        .equals(foundLanguage.getId()));
    }

}
