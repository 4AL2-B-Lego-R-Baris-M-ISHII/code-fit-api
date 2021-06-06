package fr.esgi.pa.server.exercise_case.core.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;

import java.util.Set;

public interface ExerciseCaseDao {
    Set<ExerciseCase> findAllByExerciseId(Long exerciseId) throws NotFoundException;

    void deleteAllByExerciseId(Long exerciseId);
}
