package fr.esgi.pa.server.language.usecase;

import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FindAllLanguages {
    private final LanguageDao languageDao;

    public Set<Language> execute() {
        return new HashSet<>(languageDao.findAll());
    }
}
