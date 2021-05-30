package fr.esgi.pa.server.exercise.core.util;

import fr.esgi.pa.server.language.core.Language;

public interface DefaultExercise {
    Long createDefaultExercise(String title, String description, Language language, Long userId);
}
