package fr.esgi.pa.server.unit.code.infrastructure.device;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.infrastructure.device.compiler.CCompiler;
import fr.esgi.pa.server.code.infrastructure.device.helper.CodeStateHelper;
import fr.esgi.pa.server.code.infrastructure.device.compile_runner.DockerCompileRunner;
import fr.esgi.pa.server.code.infrastructure.device.utils.ScriptCompilerContent;
import fr.esgi.pa.server.common.core.utils.io.FileDeleter;
import fr.esgi.pa.server.common.core.utils.io.FileReader;
import fr.esgi.pa.server.common.core.utils.io.FileWriter;
import fr.esgi.pa.server.common.core.utils.process.ProcessResult;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CCompilerTest {

    @Mock
    private FileReader mockFileReader;

    @Mock
    private FileWriter mockFileWriter;

    @Mock
    private FileDeleter mockFileDeleter;

    @Mock
    private DockerCompileRunner mockDockerCompilerRunner;

    @Mock
    private CodeStateHelper mockCodeStateHelper;

    private CCompiler sut;

    private final String content = "content";
    private Language cLanguage;

    private final String imageName = "id_c_compiler";
    private final String containerName = "container_name";

    @BeforeEach
    void setup() {
        cLanguage = new Language().setId(1L).setLanguageName(LanguageName.C).setFileExtension("c");

        sut = new CCompiler(mockFileDeleter, mockDockerCompilerRunner, mockCodeStateHelper);
    }

    @Disabled
    @Test
    void when_docker_file_not_exist_should_throw_exception() {

        assertThatThrownBy(() -> sut.compile(content, cLanguage, imageName, containerName))
                .isExactlyInstanceOf(FileNotFoundException.class)
                .hasMessage(CCompiler.class + " : docker file of compiler not found");
    }
}