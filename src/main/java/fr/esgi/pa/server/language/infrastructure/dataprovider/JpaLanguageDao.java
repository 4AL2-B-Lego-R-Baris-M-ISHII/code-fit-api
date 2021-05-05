package fr.esgi.pa.server.language.infrastructure.dataprovider;

import fr.esgi.pa.server.common.exception.AlreadyCreatedException;
import fr.esgi.pa.server.common.exception.NotFoundException;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaLanguageDao implements LanguageDao {
    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;

    @Override
    public Long createLanguage(LanguageName languageName, String fileExtension) throws AlreadyCreatedException {
        var maybeLanguage = languageRepository.findByName(languageName);
        if (maybeLanguage.isPresent()) {
            var message = String.format("%s : language with language name '%s' already created", this.getClass(), languageName);
            throw new AlreadyCreatedException(message);
        }

        var newLanguage = new JpaLanguage().setName(languageName).setFileExtension(fileExtension);
        return languageRepository.save(newLanguage).getId();
    }

    @Override
    public Language findByName(LanguageName languageName) throws NotFoundException {
        return languageRepository.findByName(languageName)
                .map(languageMapper::entityToDomain)
                .orElseThrow(() -> {
                    var message = String.format("%s : language name '%s' not found", this.getClass(), languageName);
                    return new NotFoundException(message);
                });
    }

    @Override
    public List<Language> findAll() {
        return languageRepository.findAll().stream()
                .map(languageMapper::entityToDomain)
                .collect(Collectors.toList());
    }
}
