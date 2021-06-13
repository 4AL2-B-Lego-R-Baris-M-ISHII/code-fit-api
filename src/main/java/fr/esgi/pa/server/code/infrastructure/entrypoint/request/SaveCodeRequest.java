package fr.esgi.pa.server.code.infrastructure.entrypoint.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class SaveCodeRequest {

    @NotNull
    @Min(value = 1L)
    private Long exerciseCaseId;

    @NotBlank
    @Size(max = 60000)
    private String codeContent;

    private Boolean toCompile = false;
}
