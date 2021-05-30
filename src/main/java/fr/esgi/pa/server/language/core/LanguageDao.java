package fr.esgi.pa.server.language.core;

import fr.esgi.pa.server.common.core.exception.AlreadyCreatedException;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.language.core.exception.IncorrectLanguageNameException;

import java.util.List;

public interface LanguageDao {
    Long createLanguage(LanguageName languageName, String fileExtension) throws AlreadyCreatedException;

    Language findByLanguageName(LanguageName languageName) throws NotFoundException;

    Language findByStrLanguage(String strLanguage) throws IncorrectLanguageNameException, NotFoundException;

    List<Language> findAll();
}
