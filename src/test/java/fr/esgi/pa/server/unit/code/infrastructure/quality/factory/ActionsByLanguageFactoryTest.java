package fr.esgi.pa.server.unit.code.infrastructure.quality.factory;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByC;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByJava;
import fr.esgi.pa.server.code.infrastructure.quality.factory.ActionsByLanguageFactory;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ActionsByLanguageFactoryTest {
    private ActionsByLanguageFactory sut;

    @BeforeEach
    void setup() {
        sut = new ActionsByLanguageFactory();
    }

    @Test
    void when_language_is_C_should_return_instance_of_ActionsByC() {
        var result = sut.getActionsByLanguage(new Language().setLanguageName(LanguageName.C));

        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(ActionsByC.class);
    }

    @Test
    void when_language_is_java_should_return_instance_of_ActionsByJava() {
        var result = sut.getActionsByLanguage(new Language().setLanguageName(LanguageName.JAVA));

        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(ActionsByJava.class);
    }
}