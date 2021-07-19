package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllExerciseThatUserResolved {
    private final ExerciseCaseDao exerciseCaseDao;
    private final ExerciseDao exerciseDao;

    public Set<DtoExercise> execute(Set<DtoExerciseCase> setDtoExerciseCaseUserResolved) {
        var setExerciseCaseId = setDtoExerciseCaseUserResolved.stream()
                .map(DtoExerciseCase::getId)
                .collect(Collectors.toSet());
        var foundExerciseCase = exerciseCaseDao.findAllByIdIn(setExerciseCaseId);
        var setExerciseId = foundExerciseCase.stream()
                .map(ExerciseCase::getExerciseId)
                .collect(Collectors.toSet());
        exerciseDao.findAllByIdIn(setExerciseId);
        return null;
    }
}
