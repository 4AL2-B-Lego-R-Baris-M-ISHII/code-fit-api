package fr.esgi.pa.server.integration.code.infrastructure.quality.factory;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByC;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByJava;
import fr.esgi.pa.server.code.infrastructure.quality.factory.ActionsByLanguageFactory;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ActionsByLanguageFactoryTest {
    @Autowired
    private ActionsByLanguageFactory sut;

    @Test
    void when_language_is_C_should_return_instance_of_ActionsByC() {
        var result = sut.getActionsByLanguage(new Language().setLanguageName(LanguageName.C11));

        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(ActionsByC.class);
    }

    @Test
    void when_language_is_java_should_return_instance_of_ActionsByJava() {
        var result = sut.getActionsByLanguage(new Language().setLanguageName(LanguageName.JAVA8));

        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(ActionsByJava.class);
    }
}