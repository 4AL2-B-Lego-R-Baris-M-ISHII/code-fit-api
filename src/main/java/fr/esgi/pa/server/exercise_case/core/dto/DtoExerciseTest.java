package fr.esgi.pa.server.exercise_case.core.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DtoExerciseTest {
    private Long id;
    private String content;
}
