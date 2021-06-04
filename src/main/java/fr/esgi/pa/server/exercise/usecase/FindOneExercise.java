package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.exercise.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseAdapter;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseCaseAdapter;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.adapter.ExerciseTestAdapter;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindOneExercise {
    private final UserDao userDao;
    private final ExerciseDao exerciseDao;
    private final ExerciseCaseDao exerciseCaseDao;
    private final ExerciseTestDao exerciseTestDao;
    private final ExerciseAdapter exerciseAdapter;
    private final ExerciseCaseAdapter exerciseCaseAdapter;
    private final ExerciseTestAdapter exerciseTestAdapter;

    public DtoExercise execute(Long exerciseId, Long userId) throws NotFoundException {
        if (!userDao.existsById(userId)) {
            var message = String.format("%s : User with userId '%d' not found", this.getClass(), userId);
            throw new NotFoundException(message);
        }

        var exercise = exerciseDao.findById(exerciseId);
        var setDtoExerciseCase = getDtoExerciseCases(exercise);

        var dtoExercise = exerciseAdapter.domainToDto(exercise);
        dtoExercise.setCases(setDtoExerciseCase);
        return dtoExercise;
    }

    private Set<DtoExerciseCase> getDtoExerciseCases(fr.esgi.pa.server.exercise.core.entity.Exercise exercise) throws NotFoundException {
        var setExerciseCase = exerciseCaseDao.findAllByExerciseId(exercise.getId());
        var setDtoExerciseCase = new HashSet<DtoExerciseCase>();
        for (ExerciseCase exerciseCase : setExerciseCase) {
            var currentDtoExerciseCase = getDtoExerciseCase(exerciseCase);
            setDtoExerciseCase.add(currentDtoExerciseCase);
        }
        return setDtoExerciseCase;
    }

    private DtoExerciseCase getDtoExerciseCase(ExerciseCase exerciseCase) throws NotFoundException {
        var currentDtoExerciseCase = exerciseCaseAdapter.domainToDto(exerciseCase);
        var setExerciseTest = exerciseTestDao.findAllByExerciseCaseId(exerciseCase.getId())
                .stream()
                .map(exerciseTestAdapter::domainToDto)
                .collect(Collectors.toSet());
        currentDtoExerciseCase.setTests(setExerciseTest);
        return currentDtoExerciseCase;
    }
}
