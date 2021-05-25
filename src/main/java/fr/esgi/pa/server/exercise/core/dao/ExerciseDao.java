package fr.esgi.pa.server.exercise.core.dao;

import fr.esgi.pa.server.language.core.Language;

public interface ExerciseDao {
    Long saveExercise(String title, String description, Language language);
}
