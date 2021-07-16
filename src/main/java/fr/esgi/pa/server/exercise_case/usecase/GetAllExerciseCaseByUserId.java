package fr.esgi.pa.server.exercise_case.usecase;

import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GetAllExerciseCaseByUserId {
    public Set<DtoExerciseCase> execute(Long userId) {
        return null;
    }
}
