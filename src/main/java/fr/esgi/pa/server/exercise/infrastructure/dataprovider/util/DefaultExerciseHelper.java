package fr.esgi.pa.server.exercise.infrastructure.dataprovider.util;

import fr.esgi.pa.server.language.core.Language;

public interface DefaultExerciseHelper {
    DefaultExerciseValues getValuesByLanguage(Language language);
}
