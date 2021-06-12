package fr.esgi.pa.server.exercise_case.core.utils;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.language.core.Language;

public interface DefaultExerciseCase {
    Long createExerciseCase(Long exerciseId, Language language) throws NotFoundException;
}
