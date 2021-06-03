package fr.esgi.pa.server.exercise.core.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.entity.ExerciseCase;

import java.util.Set;

public interface ExerciseCaseDao {
    Set<ExerciseCase> findAllByExerciseId(Long exerciseId) throws NotFoundException;
}
