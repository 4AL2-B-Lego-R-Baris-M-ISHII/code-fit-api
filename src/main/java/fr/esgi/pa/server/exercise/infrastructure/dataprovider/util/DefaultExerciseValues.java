package fr.esgi.pa.server.exercise.infrastructure.dataprovider.util;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DefaultExerciseValues {
    private String startContent;
    private String solution;
    private String testContent;
}
