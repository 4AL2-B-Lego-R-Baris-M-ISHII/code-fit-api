package fr.esgi.pa.server.exercise_case.core.utils;

import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.utils.DefaultExerciseCaseValues;
import fr.esgi.pa.server.language.core.Language;

public interface DefaultExerciseCaseHelper {
    DefaultExerciseCaseValues getValuesByLanguage(Language language);
}
