package fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class UpdateExerciseTestRequest {
    private Long id;

    @NotBlank
    @Size(max = 60000)
    private String content;
}
