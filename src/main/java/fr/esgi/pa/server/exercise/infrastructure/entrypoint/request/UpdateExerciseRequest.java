package fr.esgi.pa.server.exercise.infrastructure.entrypoint.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class UpdateExerciseRequest {
    @Size(max = 100)
    private String title;

    @Size(max = 60000)
    private String description;
}
