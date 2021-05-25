package fr.esgi.pa.server.unit.code.infrastructure.device.compiler;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.infrastructure.device.compile_runner.CompileRunner;
import fr.esgi.pa.server.code.infrastructure.device.compiler.CCompiler;
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
class CCompilerTest {
    @Mock
    private FileDeleter mockFileDeleter;

    @Mock
    private CompileRunner mockDockerCompilerRunner;

    @Mock
    private CodeStateHelper mockCodeStateHelper;

    @Mock
    private CompilerConfigRepository mockCompilerConfigRepository;

    @Mock
    private CompilerConfig mockCompilerConfig;

    private CCompiler sut;

    private final String content = "content";
    private Language cLanguage;

    @BeforeEach
    void setup() {
        cLanguage = new Language().setId(1L).setLanguageName(LanguageName.C).setFileExtension("c");

        sut = new CCompiler(mockFileDeleter, mockDockerCompilerRunner, mockCodeStateHelper, mockCompilerConfigRepository);
    }

    @Test
    void when_get_process_result_of_compile_runner_should_get_code_state_of_process() {
        when(mockCompilerConfigRepository.findByLanguageName(cLanguage.getLanguageName())).thenReturn(mockCompilerConfig);
        var processResult = new ProcessResult().setStatus(0).setOut("output");
        when(mockDockerCompilerRunner.start(mockCompilerConfig, content, cLanguage)).thenReturn(processResult);

        sut.compile(content, cLanguage);

        verify(mockCodeStateHelper).getCodeState(0);
    }

    @Test
    void when_get_code_state_of_process_should_delete_all_files_on_concerned_tmp_folder() {
        when(mockCompilerConfigRepository.findByLanguageName(cLanguage.getLanguageName())).thenReturn(mockCompilerConfig);
        var processResult = new ProcessResult().setStatus(0).setOut("output");
        when(mockDockerCompilerRunner.start(mockCompilerConfig, content, cLanguage)).thenReturn(processResult);
        when(mockCodeStateHelper.getCodeState(0)).thenReturn(CodeState.SUCCESS);
        when(mockCompilerConfig.getFolderTmpPath()).thenReturn("folder/tmp/path");

        sut.compile(content, cLanguage);

        verify(mockFileDeleter, times(1)).removeAllFiles("folder/tmp/path");
    }

    @Test
    void when_get_process_result_and_all_concerned_files_deleted_should_return_code() {
        when(mockCompilerConfigRepository.findByLanguageName(cLanguage.getLanguageName())).thenReturn(mockCompilerConfig);
        var processResult = new ProcessResult().setStatus(0).setOut("output");
        when(mockDockerCompilerRunner.start(mockCompilerConfig, content, cLanguage)).thenReturn(processResult);
        when(mockCodeStateHelper.getCodeState(0)).thenReturn(CodeState.SUCCESS);
        when(mockCompilerConfig.getFolderTmpPath()).thenReturn("folder/tmp/path");
        mockFileDeleter.removeAllFiles("folder/tmp/path");

        var result = sut.compile(content, cLanguage);

        var expectedCode = new Code().setLanguage(cLanguage).setCodeState(CodeState.SUCCESS).setOutput("output");

        assertThat(result).isEqualTo(expectedCode);
    }
}