package fr.esgi.pa.server.integration.code.infrastructure.device;

import fr.esgi.pa.server.code.infrastructure.device.CCompiler;
import fr.esgi.pa.server.code.infrastructure.device.CompilerRepositoryImpl;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CompilerRepositoryImplTest {
    @Autowired
    private ApplicationContext context;

    private CompilerRepositoryImpl sut;

    @BeforeEach
    void setup() {
        sut = new CompilerRepositoryImpl(context);
    }

    @Test
    void when_language_name_is_c_should_get_CCompiler() {
        var result = sut.findByLanguage(new Language().setLanguageName(LanguageName.C));
        assertThat(result).isInstanceOf(CCompiler.class);
    }
}