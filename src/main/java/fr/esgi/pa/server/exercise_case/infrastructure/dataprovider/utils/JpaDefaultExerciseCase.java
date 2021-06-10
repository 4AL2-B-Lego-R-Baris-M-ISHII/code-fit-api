package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.utils;

import fr.esgi.pa.server.exercise_case.core.utils.DefaultExerciseCase;
import fr.esgi.pa.server.language.core.Language;
import org.springframework.stereotype.Component;

@Component
public class JpaDefaultExerciseCase implements DefaultExerciseCase {
    @Override
    public Long createExerciseCase(Long exerciseId, Language language) {
        return null;
    }
}
