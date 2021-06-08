package fr.esgi.pa.server.exercise_case.usecase;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifyExerciseCase {
    private final ExerciseCaseDao exerciseCaseDao;
    private final ExerciseTestDao exerciseTestDao;

    public DtoExerciseCase execute(Long exerciseCaseId) throws NotFoundException {

        exerciseCaseDao.findById(exerciseCaseId);
        exerciseTestDao.findAllByExerciseCaseId(exerciseCaseId);
        return null;
    }
}
