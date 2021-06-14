package fr.esgi.pa.server.exercise.core.dto;

import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class DtoExercise {
    private Long id;
    private String title;
    private String description;
    private Long userId;
    private Set<DtoExerciseCase> cases;
}
