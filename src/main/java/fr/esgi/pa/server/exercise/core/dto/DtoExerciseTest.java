package fr.esgi.pa.server.exercise.core.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DtoExerciseTest {
    private Long id;
    private String content;
}
