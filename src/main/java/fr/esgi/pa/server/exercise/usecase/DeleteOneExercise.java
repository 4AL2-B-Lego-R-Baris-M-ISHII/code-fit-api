package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.user.core.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteOneExercise {
    private final UserDao userDao;
    private final ExerciseDao exerciseDao;

    public void execute(Long userId, Long exerciseId) throws NotFoundException {
        if (!userDao.existsById(userId)) {
            throwNotFoundExceptionWithMessage(userId, "%s : User with id '%d' not found");
            return;
        }
        if (!exerciseDao.existsById(exerciseId)) {
            throwNotFoundExceptionWithMessage(exerciseId, "%s : Exercise with id '%d' not found");
        }
        exerciseDao.deleteById(exerciseId);
    }

    private void throwNotFoundExceptionWithMessage(Long userId, String formatErrorMessage) throws NotFoundException {
        var message = String.format(formatErrorMessage,
                CommonExceptionState.NOT_FOUND,
                userId);
        throw new NotFoundException(message);
    }
}
