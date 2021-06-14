package fr.esgi.pa.server.exercise_case.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ExerciseCase {
    private Long id;
    private String solution;
    private String startContent;
    private Boolean isValid;
    private Long exerciseId;
    private Long languageId;
}
