package fr.esgi.pa.server.code.infrastructure.entrypoint;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class TestCompileCodeRequest {
    @NotBlank
    private String content;

    @NotBlank
    @Size(max = 10)
    private String language;
}
