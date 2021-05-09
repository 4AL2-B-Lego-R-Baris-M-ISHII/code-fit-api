package fr.esgi.pa.server.unit.code.infrastructure.device;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.infrastructure.device.CCompiler;
import fr.esgi.pa.server.common.core.utils.io.FileReader;
import fr.esgi.pa.server.common.core.utils.io.FileWriter;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.process.core.ProcessHelper;
import fr.esgi.pa.server.process.core.ProcessResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CCompilerTest {
    @Mock
    private FileReader mockFileReader;

    @Mock
    private FileWriter mockFileWriter;

    @Mock
    private ProcessHelper mockProcessHelper;

    @Mock
    private Process mockProcess;

    private CCompiler sut;

    private String content;
    private Language cLanguage;

    private String dockerFile;
    private String idCCompilation;

    @BeforeEach
    void setup() {
        content = "content";
        cLanguage = new Language().setId(1L).setLanguageName(LanguageName.C).setFileExtension("c");
        idCCompilation = "id_c_compiler";

        dockerFile = CCompiler.C_COMPILER_FOLDER + File.separator + "Dockerfile";
        sut = new CCompiler(mockFileReader, mockFileWriter, mockProcessHelper);
    }

    @Test
    void when_docker_file_not_exist_should_throw_exception() {
        when(mockFileReader.isFileExist(dockerFile)).thenReturn(false);

        assertThatThrownBy(() -> sut.compile(content, cLanguage, "id_c_compiler"))
                .isExactlyInstanceOf(FileNotFoundException.class)
                .hasMessage(CCompiler.class + " : docker file of compiler not found");
    }

    @Test
    void when_docker_build_command_launch_and_process_result_is_not_0_should_throw() throws IOException, InterruptedException {
        when(mockFileReader.isFileExist(dockerFile)).thenReturn(true);
        var dockerBuildCommandExpected = new String[]{"docker", "image", "build", CCompiler.C_COMPILER_FOLDER, "-t", idCCompilation};
        when(mockProcessHelper.createCommandProcess(dockerBuildCommandExpected)).thenReturn(mockProcess);
        when(mockProcess.waitFor()).thenReturn(1);

        assertThatThrownBy(() -> sut.compile(content, cLanguage, idCCompilation))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessage(CCompiler.class + " : problem docker run");
    }

    @DisplayName("when docker image build")
    @Nested
    class WhenDockerImageBuild {
        @BeforeEach
        void setup() throws InterruptedException, IOException {
            when(mockFileReader.isFileExist(dockerFile)).thenReturn(true);
            var dockerBuildCommandExpected = new String[]{"docker", "image", "build", CCompiler.C_COMPILER_FOLDER, "-t", idCCompilation};
            when(mockProcessHelper.createCommandProcess(dockerBuildCommandExpected)).thenReturn(mockProcess);
            when(mockProcess.waitFor()).thenReturn(0);
        }

        @Test
        void when_launch_command_to_run_docker_image_and_process_status_0_should_return_code_with_success_state() throws IOException, InterruptedException {
            var dockerRunCommand = new String[]{"docker", "run", "--rm", idCCompilation};
            var processResult = new ProcessResult().setOut("output").setStatus(0);
            when(mockProcessHelper.launchCommandProcess(dockerRunCommand)).thenReturn(processResult);

            var result = sut.compile(content, cLanguage, idCCompilation);

            assertThat(result.getCodeState()).isEqualTo(CodeState.SUCCESS);
        }

        @Test
        void when_launch_command_to_run_docker_image_and_process_status_1_should_return_code_with_runtime_error_state() throws IOException, InterruptedException {
            var dockerRunCommand = new String[]{"docker", "run", "--rm", idCCompilation};
            var processResult = new ProcessResult().setOut("output").setStatus(1);
            when(mockProcessHelper.launchCommandProcess(dockerRunCommand)).thenReturn(processResult);

            var result = sut.compile(content, cLanguage, idCCompilation);

            assertThat(result.getCodeState()).isEqualTo(CodeState.RUNTIME_ERROR);
        }

        @Test
        void when_launch_command_to_run_docker_image_and_process_status_2_should_return_code_with_compilation_error_state() throws IOException, InterruptedException {
            var dockerRunCommand = new String[]{"docker", "run", "--rm", idCCompilation};
            var processResult = new ProcessResult().setOut("output").setStatus(2);
            when(mockProcessHelper.launchCommandProcess(dockerRunCommand)).thenReturn(processResult);

            var result = sut.compile(content, cLanguage, idCCompilation);

            assertThat(result.getCodeState()).isEqualTo(CodeState.COMPILATION_ERROR);
        }

        @Test
        void when_launch_command_to_run_docker_image_and_process_status_139_should_return_code_with_out_of_memory_state() throws IOException, InterruptedException {
            var dockerRunCommand = new String[]{"docker", "run", "--rm", idCCompilation};
            var processResult = new ProcessResult().setOut("output").setStatus(139);
            when(mockProcessHelper.launchCommandProcess(dockerRunCommand)).thenReturn(processResult);

            var result = sut.compile(content, cLanguage, idCCompilation);

            assertThat(result.getCodeState()).isEqualTo(CodeState.OUT_OF_MEMORY);
        }

        @ParameterizedTest
        @ValueSource(ints = {5, 10, 15, 121})
        void when_launch_command_to_run_docker_image_and_process_status_others_should_return_code_with_time_limit_exceed_state(Integer otherStatus) throws IOException, InterruptedException {
            var dockerRunCommand = new String[]{"docker", "run", "--rm", idCCompilation};
            var processResult = new ProcessResult().setOut("output").setStatus(otherStatus);
            when(mockProcessHelper.launchCommandProcess(dockerRunCommand)).thenReturn(processResult);

            var result = sut.compile(content, cLanguage, idCCompilation);

            assertThat(result.getCodeState()).isEqualTo(CodeState.TIME_LIMIT_EXCEED);
        }
    }
}