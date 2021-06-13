package fr.esgi.pa.server.code.core.compiler;

import fr.esgi.pa.server.language.core.Language;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CodeResult {

    private Long id;

    private CodeState codeState;

    private String output;

    private Language language;
}
