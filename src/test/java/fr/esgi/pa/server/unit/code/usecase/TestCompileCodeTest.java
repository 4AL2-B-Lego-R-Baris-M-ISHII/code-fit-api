package fr.esgi.pa.server.unit.code.usecase;

import fr.esgi.pa.server.code.core.Compiler;
import fr.esgi.pa.server.code.core.CompilerRepository;
import fr.esgi.pa.server.code.usecase.TestCompileCode;
import fr.esgi.pa.server.language.core.LanguageDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestCompileCodeTest {
    @Mock
    private CompilerRepository mockCompilerRepository;

    @Mock
    private LanguageDao mockLanguageDao;

    @Mock
    private Compiler mockCompiler;

    private TestCompileCode sut;

    @BeforeEach
    void setup() {
        sut = new TestCompileCode(mockCompilerRepository, mockLanguageDao);
    }
//
//    @Test
//    void when_compiler_found_by_language_should_compile() throws NotFoundException, IOException, InterruptedException, CompilationException {
//        var languageC = new Language().setLanguageName(LanguageName.C).setId(1L).setFileExtension("c");
//        when(mockLanguageDao.findByName(LanguageName.C)).thenReturn(languageC);
//        when(mockCompilerRepository.findByLanguage(languageC)).thenReturn(mockCompiler);
//
//        sut.execute("content", "C");
//
//        verify(mockCompiler, times(1)).compile("content", languageC, "code_c");
//    }
//
//    @Test
//    void when_compiler_compile_should_return_code_result() throws NotFoundException, IOException, InterruptedException, CompilationException {
//        var languageC = new Language().setLanguageName(LanguageName.C).setId(1L).setFileExtension("c");
//        when(mockLanguageDao.findByName(LanguageName.C)).thenReturn(languageC);
//        when(mockCompilerRepository.findByLanguage(languageC)).thenReturn(mockCompiler);
//
//        var expectedCode = new Code().setOutput("output").setLanguage(languageC).setCodeState(CodeState.SUCCESS);
//        when(mockCompiler
//                .compile("content", languageC, "code_c")).thenReturn(expectedCode);
//
//        var result = sut.execute("content", "C");
//
//        assertThat(result).isEqualTo(expectedCode);
//    }
//
//    @Test
//    void when_compiler_throw_IOException_should_throw_compilation_exception() throws NotFoundException, IOException, InterruptedException {
//        var languageC = new Language().setLanguageName(LanguageName.C).setId(1L).setFileExtension("c");
//        when(mockLanguageDao.findByName(LanguageName.C)).thenReturn(languageC);
//        when(mockCompilerRepository.findByLanguage(languageC)).thenReturn(mockCompiler);
//
//        var message = String.format(
//                "%s : Problem compilation of language '%s' get exception '%s'",
//                CompileCode.class,
//                LanguageName.C,
//                IOException.class);
//        when(mockCompiler
//                .compile("content", languageC, "code_c")).thenThrow(new IOException());
//
//        assertThatThrownBy(() -> sut.execute("content", "C"))
//                .isExactlyInstanceOf(CompilationException.class)
//                .hasMessage(message);
//    }
//
//    @Test
//    void when_compiler_throw_InterruptedException_should_throw_compilation_exception() throws NotFoundException, IOException, InterruptedException {
//        var languageC = new Language().setLanguageName(LanguageName.C).setId(1L).setFileExtension("c");
//        when(mockLanguageDao.findByName(LanguageName.C)).thenReturn(languageC);
//        when(mockCompilerRepository.findByLanguage(languageC)).thenReturn(mockCompiler);
//
//        var message = String.format(
//                "%s : Problem compilation of language '%s' get exception '%s'",
//                CompileCode.class,
//                LanguageName.C,
//                InterruptedException.class);
//        when(mockCompiler
//                .compile("content", languageC, "code_c")).thenThrow(new InterruptedException());
//
//        assertThatThrownBy(() -> sut.execute("content", "C"))
//                .isExactlyInstanceOf(CompilationException.class)
//                .hasMessage(message);
//    }
}