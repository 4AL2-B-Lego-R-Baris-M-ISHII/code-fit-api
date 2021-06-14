package fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Accessors(chain = true)
public class UpdateExerciseCaseRequest {
    @Size(max = 60000)
    private String solution;

    @Size(max = 60000)
    private String startContent;

    @NotEmpty
    private Set<ExerciseTestRequest> tests;

    private Boolean verifyCode;
}
