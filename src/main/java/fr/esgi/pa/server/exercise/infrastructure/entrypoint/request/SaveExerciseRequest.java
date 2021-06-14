package fr.esgi.pa.server.exercise.infrastructure.entrypoint.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class SaveExerciseRequest {
    @NotBlank
    @Size(max = 30)
    private String title;

    @NotBlank
    @Size(max = 60000)
    private String description;

    @NotBlank
    @Size(max = 10)
    private String language;
}
