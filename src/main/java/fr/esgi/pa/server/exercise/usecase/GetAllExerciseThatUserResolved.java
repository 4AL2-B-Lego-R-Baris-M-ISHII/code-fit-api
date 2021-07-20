package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Objects;
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
        var setExercise = exerciseDao.findAllByIdIn(setExerciseId);
        return setExercise.stream()
                .map(exercise -> getDtoExerciseWithConcernedSetDtoExerciseCase(setDtoExerciseCaseUserResolved, foundExerciseCase, exercise))
                .collect(Collectors.toSet());
    }

    @NotNull
    private DtoExercise getDtoExerciseWithConcernedSetDtoExerciseCase(Set<DtoExerciseCase> setDtoExerciseCaseUserResolved, Set<ExerciseCase> foundExerciseCase, Exercise exercise) {
        var dtoExercise = new DtoExercise()
                .setId(exercise.getId())
                .setTitle(exercise.getTitle())
                .setDescription(exercise.getDescription());
        var concernedSetCases = getConcernedSetCases(setDtoExerciseCaseUserResolved, foundExerciseCase, exercise);
        dtoExercise.setCases(concernedSetCases);
        return dtoExercise;
    }

    @NotNull
    private Set<DtoExerciseCase> getConcernedSetCases(Set<DtoExerciseCase> setDtoExerciseCaseUserResolved, Set<ExerciseCase> foundExerciseCase, Exercise exercise) {
        return foundExerciseCase.stream()
                .filter(exerciseCase -> exerciseCase.getExerciseId().equals(exercise.getId()))
                .map(exerciseCase -> setDtoExerciseCaseUserResolved.stream()
                        .filter(dtoExerciseCase -> dtoExerciseCase.getId().equals(exerciseCase.getId()))
                        .findFirst().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
