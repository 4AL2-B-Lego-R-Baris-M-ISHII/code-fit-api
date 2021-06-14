package fr.esgi.pa.server.exercise_case.usecase;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteOneExerciseCase {
    private final ExerciseCaseDao exerciseCaseDao;
    private final ExerciseTestDao exerciseTestDao;

    public void execute(Long exerciseCaseId) throws NotFoundException {
        if (!exerciseCaseDao.existsById(exerciseCaseId)) {
            var message = String.format(
                    "%s : Exercise case with id '%d' not found",
                    CommonExceptionState.NOT_FOUND,
                    exerciseCaseId
            );
            throw new NotFoundException(message);
        }

        exerciseTestDao.deleteAllByExerciseCaseId(exerciseCaseId);
        exerciseCaseDao.deleteById(exerciseCaseId);
    }
}
