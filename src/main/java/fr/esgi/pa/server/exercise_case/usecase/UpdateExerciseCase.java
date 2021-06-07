package fr.esgi.pa.server.exercise_case.usecase;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdateExerciseCase {
    private final UserDao userDao;
    private final ExerciseCaseDao exerciseCaseDao;
    private final ExerciseDao exerciseDao;
    private final ExerciseTestDao exerciseTestDao;

    @Transactional
    public void execute(
            Long userId,
            Long exerciseCaseId,
            String solution,
            String startContent,
            Set<ExerciseTest> setTest) throws NotFoundException, ForbiddenException {
        checkIfUserExistsById(userId);
        var foundExerciseCase = exerciseCaseDao.findById(exerciseCaseId);
        checkIfUserIsCreatorOfExercise(userId, foundExerciseCase);

        foundExerciseCase.setSolution(solution);
        foundExerciseCase.setStartContent(startContent);
        checkSetTestIfContainGivenExerciseCaseId(exerciseCaseId, setTest);

        exerciseTestDao.saveAll(setTest);
        exerciseCaseDao.saveOne(foundExerciseCase);
    }

    private void checkIfUserExistsById(Long userId) throws NotFoundException {
        if (!userDao.existsById(userId)) {
            var message = String.format("%s : User with id '%d' not found", CommonExceptionState.NOT_FOUND, userId);
            throw new NotFoundException(message);
        }
    }

    private void checkIfUserIsCreatorOfExercise(Long userId, fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase foundExerciseCase) throws NotFoundException, ForbiddenException {
        var foundExercise = exerciseDao.findById(foundExerciseCase.getExerciseId());
        if (!foundExercise.getUserId().equals(userId)) {
            var message = String.format(
                    "%s : Exercise case can be update by creator of exercise with id '%d'",
                    CommonExceptionState.FORBIDDEN,
                    foundExercise.getId()
            );
            throw new ForbiddenException(message);
        }
    }

    private void checkSetTestIfContainGivenExerciseCaseId(Long exerciseCaseId, Set<ExerciseTest> setTest) throws ForbiddenException {
        if (setTest.isEmpty()) {
            var message = String.format("%s : Set test can't be empty", ForbiddenException.class);
            throw new ForbiddenException(message);
        }
        for (ExerciseTest exerciseTest : setTest) {
            if (exerciseTest.getExerciseCaseId() == null || !exerciseTest.getExerciseCaseId().equals(exerciseCaseId)) {
                var message = String.format("%s : All exercise tests need concerned exercise case id", ForbiddenException.class);
                throw new ForbiddenException(message);
            }
        }
    }
}
