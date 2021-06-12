package fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class SaveExerciseCaseRequest {

    @NotNull
    @Min(value = 1)
    private Long exerciseId;

    @NotNull
    @Min(value = 1)
    private Long languageId;
}
