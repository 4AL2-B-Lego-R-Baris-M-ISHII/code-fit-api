package fr.esgi.pa.server.exercise_case.core.dao;

import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;

import java.util.Set;

public interface ExerciseTestDao {
    Set<ExerciseTest> findAllByExerciseCaseId(Long exerciseCaseId);

    void deleteAllByExerciseCaseId(Long exerciseCaseId);

    Set<ExerciseTest> saveAll(Set<ExerciseTest> setTest);
}
