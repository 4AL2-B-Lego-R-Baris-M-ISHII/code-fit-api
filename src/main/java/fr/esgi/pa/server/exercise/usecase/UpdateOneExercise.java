package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateOneExercise {
    private final UserDao userDao;
    private final ExerciseDao exerciseDao;

    @Transactional
    public void execute(Long userId, Long exerciseId, String title, String description) throws NotFoundException {
        checkIfUserWithUserIdExists(userId);
        exerciseDao.findById(exerciseId);
    }

    private void checkIfUserWithUserIdExists(Long userId) throws NotFoundException {
        if (!userDao.existsById(userId)) {
            var message = String.format("%s : User with id '%d' not found", NotFoundException.class, userId);
            throw new NotFoundException(message);
        }
    }
}
