package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DefaultExerciseCaseValues {
    private String startContent;
    private String solution;
    private String testContent;
}
