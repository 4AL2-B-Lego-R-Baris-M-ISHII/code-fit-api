package fr.esgi.pa.server.exercise_case.core.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;

import java.util.Set;

public interface ExerciseCaseDao {
    Set<ExerciseCase> findAllByExerciseId(Long exerciseId);

    void deleteAllByExerciseId(Long exerciseId);

    Boolean existsById(Long exerciseCaseId);

    ExerciseCase findById(Long exerciseCaseId) throws NotFoundException;

    ExerciseCase saveOne(ExerciseCase exerciseCaseToSave);

    void deleteById(Long exerciseCaseId) throws NotFoundException;

    Set<ExerciseCase> findAllByIdIn(Set<Long> setId);
}
