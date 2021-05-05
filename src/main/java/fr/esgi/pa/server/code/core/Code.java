package fr.esgi.pa.server.code.core;

import fr.esgi.pa.server.language.core.Language;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Code {

    private Long id;

    private CodeState codeState;

    private String output;

    private Language language;
}
