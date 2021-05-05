package fr.esgi.pa.server.language.infrastructure.dataprovider;

import fr.esgi.pa.server.common.mapper.MapperEntityToDomain;
import fr.esgi.pa.server.language.core.Language;
import org.springframework.stereotype.Component;

@Component
public class LanguageMapper implements MapperEntityToDomain<JpaLanguage, Language> {
    @Override
    public Language entityToDomain(JpaLanguage entity) {
        return new Language()
                .setId(entity.getId())
                .setLanguageName(entity.getName())
                .setFileExtension(entity.getFileExtension());
    }
}
