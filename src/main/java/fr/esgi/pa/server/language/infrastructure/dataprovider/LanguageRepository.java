package fr.esgi.pa.server.language.infrastructure.dataprovider;

import fr.esgi.pa.server.language.core.LanguageName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<JpaLanguage, Long> {
    Optional<JpaLanguage> findByName(LanguageName languageName);
}
