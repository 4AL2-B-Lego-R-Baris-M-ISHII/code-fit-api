package fr.esgi.pa.server.exercise.core.dto;

import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.user.core.dto.DtoUser;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class DtoExercise {
    private Long id;
    private String title;
    private String description;
    private DtoUser user;
    private Set<DtoExerciseCase> cases;
}
