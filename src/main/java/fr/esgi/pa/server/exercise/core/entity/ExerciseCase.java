package fr.esgi.pa.server.exercise.core.entity;

import fr.esgi.pa.server.language.core.Language;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class ExerciseCase {
    private Long id;
    private String solution;
    private Boolean isValid;
    private Set<ExerciseTest> tests;
    private Exercise exercise;
    private Long languageId;
}
