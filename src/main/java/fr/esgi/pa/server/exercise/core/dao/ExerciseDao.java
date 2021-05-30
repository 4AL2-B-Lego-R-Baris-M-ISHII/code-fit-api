package fr.esgi.pa.server.exercise.core.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.entity.Exercise;

public interface ExerciseDao {
    Exercise createExercise(String title, String description, Long userId) throws NotFoundException;
}
