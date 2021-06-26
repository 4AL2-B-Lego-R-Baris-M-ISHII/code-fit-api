package fr.esgi.pa.server.unit.code.infrastructure.device.compiler;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.compiler.CodeState;
import fr.esgi.pa.server.code.infrastructure.device.compile_runner.CompileRunner;
import fr.esgi.pa.server.code.infrastructure.device.compiler.JavaCompiler;
import fr.esgi.pa.server.code.infrastructure.device.compiler.config.CompilerConfig;
import fr.esgi.pa.server.code.infrastructure.device.helper.CodeStateHelper;
import fr.esgi.pa.server.code.infrastructure.device.repository.CompilerConfigRepository;
import fr.esgi.pa.server.common.core.utils.io.FileDeleter;
import fr.esgi.pa.server.common.core.utils.process.ProcessResult;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JavaCompilerTest {
    @Mock
    private CompileRunner mockDockerCompiler;

    @Mock
    private CodeStateHelper mockCodeStateHelper;

    @Mock
    private FileDeleter mockFileDeleter;

    @Mock
    private CompilerConfigRepository mockCompilerConfigRepository;

    @Mock
    private CompilerConfig mockCompilerConfig;

    private Language javaLanguage;

    private JavaCompiler sut;

    @BeforeEach
    void setup() {
        sut = new JavaCompiler(
                mockDockerCompiler,
                mockCodeStateHelper,
                mockFileDeleter,
                mockCompilerConfigRepository
        );

        javaLanguage = new Language()
                .setId(2L)
                .setLanguageName(LanguageName.JAVA8)
                .setFileExtension("java");
    }

    @Test
    void when_get_process_result_of_compilation_should_get_code_state() {
        when(mockCompilerConfigRepository.findByLanguageName(LanguageName.JAVA8)).thenReturn(mockCompilerConfig);
        var processResult = new ProcessResult().setOut("output").setStatus(0);
        when(mockDockerCompiler.start(mockCompilerConfig, "content", javaLanguage)).thenReturn(processResult);

        sut.compile("content", javaLanguage);

        verify(mockCodeStateHelper, times(1)).getCodeState(0);
    }

    @Test
    void when_get_process_result_and_code_state_should_remove_files_of_concerned_tmp_folder() {
        when(mockCompilerConfigRepository.findByLanguageName(LanguageName.JAVA8)).thenReturn(mockCompilerConfig);
        var processResult = new ProcessResult().setOut("output").setStatus(0);
        when(mockDockerCompiler.start(mockCompilerConfig, "content", javaLanguage)).thenReturn(processResult);
        when(mockCodeStateHelper.getCodeState(0)).thenReturn(CodeState.SUCCESS);
        var folderTmpPath = "folder/tmp";
        when(mockCompilerConfig.getFolderTmpPath()).thenReturn(folderTmpPath);

        sut.compile("content", javaLanguage);

        verify(mockFileDeleter, times(1)).removeAllFiles(folderTmpPath);
    }

    @Test
    void when_get_process_result_and_concerned_files_deleted_should_return_code_result() {
        when(mockCompilerConfigRepository.findByLanguageName(LanguageName.JAVA8)).thenReturn(mockCompilerConfig);
        var processResult = new ProcessResult().setOut("output").setStatus(0);
        when(mockDockerCompiler.start(mockCompilerConfig, "content", javaLanguage)).thenReturn(processResult);
        when(mockCodeStateHelper.getCodeState(0)).thenReturn(CodeState.SUCCESS);
        var folderTmpPath = "folder/tmp";
        when(mockCompilerConfig.getFolderTmpPath()).thenReturn(folderTmpPath);
        when(mockFileDeleter.removeAllFiles(folderTmpPath)).thenReturn(true);

        var result = sut.compile("content", javaLanguage);

        var expectedCode = new CodeResult()
                .setCodeState(CodeState.SUCCESS)
                .setLanguage(javaLanguage)
                .setOutput("output");

        assertThat(result).isEqualTo(expectedCode);
    }
}