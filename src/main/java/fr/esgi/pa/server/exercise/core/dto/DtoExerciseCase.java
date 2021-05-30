package fr.esgi.pa.server.exercise.core.dto;

import fr.esgi.pa.server.language.core.Language;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class DtoExerciseCase {
    private Long id;
    private String solution;
    private String startContent;
    private Boolean isValid;
    private Set<DtoExerciseTest> tests;
    private Language language;
}
