package fr.esgi.pa.server.code.core;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Language {
    private Long id;
    private LanguageName languageName;
    private String fileExtension;
}
