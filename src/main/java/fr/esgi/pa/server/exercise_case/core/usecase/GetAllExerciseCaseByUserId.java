package fr.esgi.pa.server.exercise_case.core.usecase;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;

import java.util.Set;

public interface GetAllExerciseCaseByUserId {
    Set<DtoExerciseCase> execute(Long userId) throws NotFoundException;
}
