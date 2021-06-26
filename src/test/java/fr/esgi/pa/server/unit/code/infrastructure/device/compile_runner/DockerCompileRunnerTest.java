package fr.esgi.pa.server.unit.code.infrastructure.device.compile_runner;

import fr.esgi.pa.server.code.infrastructure.device.compile_runner.DockerCompileRunner;
import fr.esgi.pa.server.code.infrastructure.device.compiler.config.CompilerConfig;
import fr.esgi.pa.server.code.infrastructure.device.utils.ScriptCompilerContent;
import fr.esgi.pa.server.common.core.utils.io.FileFactory;
import fr.esgi.pa.server.common.core.utils.io.FileReader;
import fr.esgi.pa.server.common.core.utils.io.FileWriter;
import fr.esgi.pa.server.common.core.utils.process.ProcessHelper;
import fr.esgi.pa.server.common.core.utils.process.ProcessResult;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import lombok.SneakyThrows;
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
class DockerCompileRunnerTest {

    @Mock
    private ProcessHelper mockProcessHelper;

    @Mock
    private Process mockProcess;

    @Mock
    private FileReader mockFileReader;

    @Mock
    private FileWriter mockFileWriter;

    @Mock
    private FileFactory mockFileFactory;

    @Mock
    private CompilerConfig mockCompilerConfig;

    @Mock
    private ScriptCompilerContent mockScriptCompilerContent;

    private Language currentLanguage;

    private DockerCompileRunner sut;

    private final String folderPath = "folder" + File.separator + "path";

    @BeforeEach
    void setup() {
        sut = new DockerCompileRunner(
                mockProcessHelper,
                mockFileFactory,
                mockFileReader,
                mockFileWriter,
                mockScriptCompilerContent);
        currentLanguage = new Language().setId(1L).setLanguageName(LanguageName.C11).setFileExtension("c");
    }

    @Test
    void when_docker_file_not_exist_should_throw() {
        var folderPath = "folder/path";
        when(mockCompilerConfig.getFolderPath()).thenReturn(folderPath);
        var dockerFilePath = folderPath + File.separator + "Dockerfile";
        when(mockFileReader.isFileExist(dockerFilePath)).thenReturn(false);

        assertThatThrownBy(() -> sut.start(mockCompilerConfig, "content", currentLanguage))
                .isExactlyInstanceOf(FileNotFoundException.class)
                .hasMessage(String.format("%s : docker file of compiler '%s' not found", sut.getClass(), currentLanguage.getLanguageName()));
    }

    @SneakyThrows
    @Test
    void when_create_directories_of_folderTmpPath_throw_exception_should_throw() {
        var folderPath = "folder/path";
        when(mockCompilerConfig.getFolderPath()).thenReturn(folderPath);
        var dockerFilePath = folderPath + File.separator + "Dockerfile";
        when(mockFileReader.isFileExist(dockerFilePath)).thenReturn(true);
        var folderTmpPath = folderPath + "/tmp";
        when(mockCompilerConfig.getFolderTmpPath()).thenReturn(folderTmpPath);
        when(mockFileReader.isFileExist(folderTmpPath)).thenReturn(false);
        doThrow(IOException.class).when(mockFileWriter).createDirectories(folderTmpPath);

        assertThatThrownBy(() -> sut.start(mockCompilerConfig, "content", currentLanguage))
                .isExactlyInstanceOf(IOException.class);
    }

    @DisplayName("when docker start command is launch")
    @Nested
    class WhenDockerStartCommandLaunch {
        @BeforeEach
        void setup() throws IOException {
            var folderPath = "folder" + File.separator + "path";
            when(mockCompilerConfig.getFolderPath()).thenReturn(folderPath);
            var dockerFilePath = folderPath + File.separator + "Dockerfile";
            when(mockFileReader.isFileExist(dockerFilePath)).thenReturn(true);
            var folderTmpPath = folderPath + File.separator + "tmp";
            when(mockCompilerConfig.getFolderTmpPath()).thenReturn(folderTmpPath);
            var mainFile = "main." + currentLanguage.getFileExtension();
            var currentMainFilePath = folderTmpPath + File.separator + mainFile;
            doNothing().when(mockFileWriter).writeContentToFile("content", currentMainFilePath);
            var scriptContent = "script content";
            when(mockScriptCompilerContent.getScriptByLanguage(currentLanguage, mainFile, mockCompilerConfig)).thenReturn(scriptContent);
            var launchScriptPath = folderTmpPath + File.separator + "launch.sh";
            doNothing().when(mockFileWriter).writeContentToFile(scriptContent, launchScriptPath);
        }

        @Test
        void when_process_output_not_error_concerned_container_should_return_process() throws IOException, InterruptedException {
            var expectedContainerName = "code_container_" + currentLanguage.getFileExtension();
            var expectedDockerRunCommand = new String[]{"docker", "start", expectedContainerName, "-i"};
            var processResult = new ProcessResult().setStatus(1).setOut("Not an error");
            when(mockProcessHelper.launchCommandProcess(expectedDockerRunCommand)).thenReturn(processResult);

            var result = sut.start(mockCompilerConfig, "content", currentLanguage);

            assertThat(result).isEqualTo(processResult);
        }

        @Test
        void when_process_output_indicate_container_error_but_status_not_1_should_return_process() throws IOException, InterruptedException {
            var expectedContainerName = "code_container_" + currentLanguage.getFileExtension();
            var expectedDockerRunCommand = new String[]{"docker", "start", expectedContainerName, "-i"};
            var processResult = new ProcessResult().setStatus(0).setOut("Error: No such container");
            when(mockProcessHelper.launchCommandProcess(expectedDockerRunCommand)).thenReturn(processResult);

            var result = sut.start(mockCompilerConfig, "content", currentLanguage);

            assertThat(result).isEqualTo(processResult);
        }
    }

    @DisplayName("when concerned docker container is not created")
    @Nested
    class WhenContainerNotCreated {

        @BeforeEach
        void setup() throws IOException, InterruptedException {
            when(mockCompilerConfig.getFolderPath()).thenReturn(folderPath);
            var dockerFilePath = folderPath + File.separator + "Dockerfile";
            when(mockFileReader.isFileExist(dockerFilePath)).thenReturn(true);
            var folderTmpPath = folderPath + File.separator + "tmp";
            when(mockCompilerConfig.getFolderTmpPath()).thenReturn(folderTmpPath);
            var mainFile = "main." + currentLanguage.getFileExtension();
            var currentMainFilePath = folderTmpPath + File.separator + mainFile;
            doNothing().when(mockFileWriter).writeContentToFile("content", currentMainFilePath);

            var scriptContent = "script content";
            when(mockScriptCompilerContent.getScriptByLanguage(currentLanguage, mainFile, mockCompilerConfig)).thenReturn(scriptContent);
            var launchScriptPath = folderTmpPath + File.separator + "launch.sh";
            doNothing().when(mockFileWriter).writeContentToFile(scriptContent, launchScriptPath);

            var containerName = "code_container_" + currentLanguage.getFileExtension();
            var dockerRunCommand = new String[]{"docker", "start", containerName, "-i"};
            var processResult = new ProcessResult().setStatus(1).setOut("Error: No such container");
            when(mockProcessHelper.launchCommandProcess(dockerRunCommand)).thenReturn(processResult);
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 124, 139})
        void when_build_image_not_success_should_throw_exception(int notSuccessStatus) throws IOException, InterruptedException {
            var expectedImageName = "code_image_" + currentLanguage.getFileExtension();
            var expectedDockerBuildCommand = new String[]{"docker", "image", "build", folderPath, "-t", expectedImageName};
            when(mockProcessHelper.launchCommandAndGetProcess(expectedDockerBuildCommand)).thenReturn(mockProcess);
            when(mockProcess.waitFor()).thenReturn(notSuccessStatus);

            assertThatThrownBy(() -> sut.start(mockCompilerConfig, "content", currentLanguage))
                    .isExactlyInstanceOf(RuntimeException.class)
                    .hasMessage(String.format("%s : problem docker image build", sut.getClass()));
        }
    }

    @DisplayName("when image is build")
    @Nested
    class WhenImageBuild {

        @Mock
        private File mockFile;

        private final String folderTmpPath = folderPath + File.separator + "tmp";

        private String containerName;

        private String imageName;

        @BeforeEach
        void setup() throws IOException, InterruptedException {
            containerName = "code_container_" + currentLanguage.getFileExtension();

            imageName = "code_image_" + currentLanguage.getFileExtension();
            when(mockCompilerConfig.getFolderPath()).thenReturn(folderPath);
            var dockerFilePath = folderPath + File.separator + "Dockerfile";
            when(mockFileReader.isFileExist(dockerFilePath)).thenReturn(true);

            when(mockCompilerConfig.getFolderTmpPath()).thenReturn(folderTmpPath);
            var mainFile = "main." + currentLanguage.getFileExtension();
            var currentMainFilePath = folderTmpPath + File.separator + mainFile;
            doNothing().when(mockFileWriter).writeContentToFile("content", currentMainFilePath);

            var scriptContent = "script content";
            when(mockScriptCompilerContent.getScriptByLanguage(currentLanguage, mainFile, mockCompilerConfig)).thenReturn(scriptContent);
            var launchScriptPath = folderTmpPath + File.separator + "launch.sh";
            doNothing().when(mockFileWriter).writeContentToFile(scriptContent, launchScriptPath);
            var expectedContainerName = "code_container_" + currentLanguage.getFileExtension();
            var expectedDockerRunCommand = new String[]{"docker", "start", expectedContainerName, "-i"};
            var processResult = new ProcessResult().setStatus(1).setOut("Error: No such container");
            when(mockProcessHelper.launchCommandProcess(expectedDockerRunCommand)).thenReturn(processResult);

            var dockerRunCommand = new String[]{"docker", "start", containerName, "-i"};
            when(mockProcessHelper.launchCommandProcess(dockerRunCommand)).thenReturn(processResult);

            var expectedDockerBuildCommand = new String[]{"docker", "image", "build", folderPath, "-t", imageName};
            when(mockProcessHelper.launchCommandAndGetProcess(expectedDockerBuildCommand)).thenReturn(mockProcess);
            when(mockProcess.waitFor()).thenReturn(0);
        }

        @Test
        void should_get_absolute_path_of_folder_tmp_path() throws IOException, InterruptedException {
            when(mockFileFactory.createFile(folderTmpPath)).thenReturn(mockFile);

            sut.start(mockCompilerConfig, "content", currentLanguage);

            verify(mockFile, times(1)).getAbsolutePath();
        }

        @Test
        void should_launch_docker_run_command_with_mount_option() throws IOException, InterruptedException {
            var absolutePath = "absolute/path";
            when(mockFileFactory.createFile(folderTmpPath)).thenReturn(mockFile);
            when(mockFile.getAbsolutePath()).thenReturn("absolute/path");

            sut.start(mockCompilerConfig, "content", currentLanguage);

            var mountArg = "type=bind,source=" + absolutePath + ",target=/app";
            var expectedDockerRunCommand = new String[]{"docker", "run", "--name", containerName, "--mount", mountArg, imageName};

            verify(mockProcessHelper, times(1)).launchCommandProcess(expectedDockerRunCommand);
        }

        @Test
        void when_docker_run_launch_should_return_process() throws IOException, InterruptedException {
            var absolutePath = "absolute/path";
            when(mockFileFactory.createFile(folderTmpPath)).thenReturn(mockFile);
            when(mockFile.getAbsolutePath()).thenReturn("absolute/path");
            var mountArg = "type=bind,source=" + absolutePath + ",target=/app";
            var dockerRunCommand = new String[]{"docker", "run", "--name", containerName, "--mount", mountArg, imageName};
            var expectedProcessResult = new ProcessResult().setStatus(0).setOut("output");
            when(mockProcessHelper.launchCommandProcess(dockerRunCommand)).thenReturn(expectedProcessResult);

            var result = sut.start(mockCompilerConfig, "content", currentLanguage);

            assertThat(result).isEqualTo(expectedProcessResult);
        }
    }
}