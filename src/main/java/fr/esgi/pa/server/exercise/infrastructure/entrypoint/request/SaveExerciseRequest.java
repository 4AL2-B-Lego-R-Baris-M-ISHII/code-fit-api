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
    @Size(max = 100)
    private String description;

    @Size(max = 10)
    private String language;
}
