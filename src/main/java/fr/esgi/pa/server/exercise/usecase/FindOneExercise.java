package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise.core.entity.ExerciseCase;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindOneExercise {
    private final UserDao userDao;
    private final ExerciseDao exerciseDao;
    private final ExerciseCaseDao exerciseCaseDao;
    private final ExerciseTestDao exerciseTestDao;

    public DtoExercise execute(Long exerciseId, Long userId) throws NotFoundException {
        if (!userDao.existsById(userId)) {
            var message = String.format("%s : User with userId '%d' not found", this.getClass(), userId);
            throw new NotFoundException(message);
        }

        var exercise = exerciseDao.findById(exerciseId);
        var setExerciseCase = exerciseCaseDao.findAllByExerciseId(exercise.getId());

        for (ExerciseCase exerciseCase : setExerciseCase) {
            exerciseTestDao.findAllByExerciseCaseId(exerciseCase.getId());
        }
        // TODO convert all element to DTO
        return null;
    }
}
