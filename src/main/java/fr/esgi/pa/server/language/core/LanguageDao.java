package fr.esgi.pa.server.language.core;

import fr.esgi.pa.server.common.exception.AlreadyCreatedException;
import fr.esgi.pa.server.common.exception.NotFoundException;

import java.util.List;

public interface LanguageDao {
    Long createLanguage(LanguageName languageName, String fileExtension) throws AlreadyCreatedException;

    Language findByName(LanguageName languageName) throws NotFoundException;

    List<Language> findAll();
}
