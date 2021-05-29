package fr.esgi.pa.server.exercise.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ExerciseTest {
    private Long id;
    private String content;
    private ExerciseCase exerciseCase;
}
