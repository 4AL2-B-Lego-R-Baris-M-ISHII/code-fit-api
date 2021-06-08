package fr.esgi.pa.server.exercise_case.core.dto;

import fr.esgi.pa.server.code.core.Code;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class DtoVerifyExerciseCase {
    Set<Code> setCode;
    Boolean isExerciseCaseValid;
}
