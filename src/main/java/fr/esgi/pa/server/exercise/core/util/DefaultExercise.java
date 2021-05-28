package fr.esgi.pa.server.exercise.core;

import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.language.core.Language;

public interface DefaultExercise {
    Exercise createDefaultExercise(String title, String description, Language language, Long userId);
}
