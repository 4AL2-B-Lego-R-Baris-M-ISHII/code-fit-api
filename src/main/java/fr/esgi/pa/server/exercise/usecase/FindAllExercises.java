package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseAdapter;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseCaseAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindAllExercises {

    private final ExerciseDao exerciseDao;
    private final ExerciseCaseDao exerciseCaseDao;
    private final ExerciseAdapter exerciseAdapter;
    private final ExerciseCaseAdapter exerciseCaseAdapter;

    public Set<DtoExercise> execute() throws NotFoundException {
        var exercises = exerciseDao.findAll();
        var result = new HashSet<DtoExercise>();

        for (Exercise exercise : exercises) {
            var dtoExercise = getDtoExerciseWithCases(exercise);
            result.add(dtoExercise);
        }

        return result;
    }

    private DtoExercise getDtoExerciseWithCases(Exercise exercise) throws NotFoundException {
        var cases = exerciseCaseDao.findAllByExerciseId(exercise.getId())
                .stream().map(exerciseCaseAdapter::domainToDto)
                .collect(Collectors.toSet());

        var dtoExercise = exerciseAdapter.domainToDto(exercise);
        if (!cases.isEmpty()) {
            dtoExercise.setCases(cases);
        }
        return dtoExercise;
    }
}
