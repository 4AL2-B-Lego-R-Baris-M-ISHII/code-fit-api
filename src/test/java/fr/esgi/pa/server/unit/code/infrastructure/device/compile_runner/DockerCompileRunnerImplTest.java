package fr.esgi.pa.server.unit.code.infrastructure.device;

import fr.esgi.pa.server.code.infrastructure.device.compile_runner.DockerCompileRunnerImpl;
import fr.esgi.pa.server.code.infrastructure.device.compiler.config.CompilerConfig;
import fr.esgi.pa.server.common.core.utils.io.FileDeleter;
import fr.esgi.pa.server.common.core.utils.io.FileFactory;
import fr.esgi.pa.server.common.core.utils.io.FileReader;
import fr.esgi.pa.server.common.core.utils.io.FileWriter;
import fr.esgi.pa.server.common.core.utils.process.ProcessHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

@ExtendWith(MockitoExtension.class)
class DockerCompileRunnerImplTest {

    @Mock
    private CompilerConfig folderPath;

    @Mock
    private ProcessHelper mockProcessHelper;

    @Mock
    private Process mockProcess;

    @Mock
    private FileFactory mockFileFactory;

    @Mock
    private File mockFile;

    @Mock
    private FileReader mockFileReader;

    @Mock
    private FileWriter mockFileWriter;

    @Mock
    private FileDeleter mockFileDeleter;

    private DockerCompileRunnerImpl sut;
    private final String dockerImage = "dockerImage";
    private final String containerName = "containerName";

    @BeforeEach
    void setup() {
        sut = new DockerCompileRunnerImpl(
                mockProcessHelper,
                mockFileFactory,
                mockFileReader,
                mockFileWriter);
    }

//    @SneakyThrows
//    @Test
//    void start_when_docker_start_command_out_is_not_error_and_status_is_0_should_return_result() {
//        var dockerRunCommand = new String[]{"docker", "start", containerName, "-i"};
//        var dockerPresentResult = new ProcessResult().setStatus(0).setOut("present");
//        when(mockProcessHelper.launchCommandProcess(dockerRunCommand)).thenReturn(dockerPresentResult);
//
//        var result = sut.start(folderPath, dockerImage, containerName);
//
//        assertThat(result).isEqualTo(dockerPresentResult);
//    }
//
//    @SneakyThrows
//    @Test
//    void start_when_docker_start_command_out_is_error_but_status_is_not_1_should_return_result() {
//        var dockerRunCommand = new String[]{"docker", "start", containerName, "-i"};
//        var dockerPresentResult = new ProcessResult().setStatus(0).setOut("Error: No such container");
//        when(mockProcessHelper.launchCommandProcess(dockerRunCommand)).thenReturn(dockerPresentResult);
//
//        var result = sut.start(folderPath, dockerImage, containerName);
//
//        assertThat(result).isEqualTo(dockerPresentResult);
//    }
//
//    @SneakyThrows
//    @ParameterizedTest
//    @ValueSource(ints = {1, 2, 124, 139})
//    void start_when_docker_image_build_command_not_success_should_throw(int notSuccessStatus) {
//        var dockerRunCommand = new String[]{"docker", "start", containerName, "-i"};
//        var dockerPresentResult = new ProcessResult().setStatus(1).setOut("Error: No such container");
//        when(mockProcessHelper.launchCommandProcess(dockerRunCommand)).thenReturn(dockerPresentResult);
//        var dockerBuildCommand = new String[]{"docker", "image", "build", folderPath, "-t", dockerImage};
//        when(mockProcessHelper.launchCommandAndGetProcess(dockerBuildCommand)).thenReturn(mockProcess);
//        when(mockProcess.waitFor()).thenReturn(notSuccessStatus);
//
//        var errorMessage = String.format("%s : problem docker run", sut.getClass());
//
//        assertThatThrownBy(() -> sut.start(folderPath, dockerImage, containerName))
//                .isExactlyInstanceOf(RuntimeException.class)
//                .hasMessage(errorMessage);
//    }
//
//    @SneakyThrows
//    @Test
//    void start_when_docker_image_build_success_should_launch_command_to_run_docker_with_bind_mount() {
//        var dockerRunCommand = new String[]{"docker", "start", containerName, "-i"};
//        var dockerPresentResult = new ProcessResult().setStatus(1).setOut("Error: No such container");
//        when(mockProcessHelper.launchCommandProcess(dockerRunCommand)).thenReturn(dockerPresentResult);
//        var dockerBuildCommand = new String[]{"docker", "image", "build", folderPath, "-t", dockerImage};
//        when(mockProcessHelper.launchCommandAndGetProcess(dockerBuildCommand)).thenReturn(mockProcess);
//        when(mockProcess.waitFor()).thenReturn(0);
//        var dockerFilePath = folderPath + File.separator + "Dockerfile";
//        when(mockFileFactory.createFile(dockerFilePath)).thenReturn(mockFile);
//        var absolutePath = "root" + File.separator + folderPath;
//        var dockerFileAbsolutePath = absolutePath + File.separator + "Dockerfile";
//        when(mockFile.getAbsolutePath()).thenReturn(dockerFileAbsolutePath);
//
//        sut.start(folderPath, dockerImage, containerName);
//
//        var absoluteTmpPath = absolutePath + File.separator + "tmp";
//        var expectedMountArg = "type=bind,source=" + absoluteTmpPath + ",target=/app";
//        var expectedDockerCommand = new String[]{"docker", "run", "--name", containerName, "--mount", expectedMountArg, dockerImage};
//        verify(mockProcessHelper, times(1)).launchCommandProcess(expectedDockerCommand);
//    }
//
//    @SneakyThrows
//    @Test
//    void start_when_docker_run_launched_should_return_result() {
//        var dockerStartCommand = new String[]{"docker", "start", containerName, "-i"};
//        var dockerPresentResult = new ProcessResult().setStatus(1).setOut("Error: No such container");
//        when(mockProcessHelper.launchCommandProcess(dockerStartCommand)).thenReturn(dockerPresentResult);
//        var dockerBuildCommand = new String[]{"docker", "image", "build", folderPath, "-t", dockerImage};
//        when(mockProcessHelper.launchCommandAndGetProcess(dockerBuildCommand)).thenReturn(mockProcess);
//        when(mockProcess.waitFor()).thenReturn(0);
//        var dockerFilePath = folderPath + File.separator + "Dockerfile";
//        when(mockFileFactory.createFile(dockerFilePath)).thenReturn(mockFile);
//        var absolutePath = "root" + File.separator + folderPath;
//        var dockerFileAbsolutePath = absolutePath + File.separator + "Dockerfile";
//        when(mockFile.getAbsolutePath()).thenReturn(dockerFileAbsolutePath);
//        var absoluteTmpPath = absolutePath + File.separator + "tmp";
//        var mountArg = "type=bind,source=" + absoluteTmpPath + ",target=/app";
//        var dockerRunCommand = new String[]{"docker", "run", "--name", containerName, "--mount", mountArg, dockerImage};
//        var expectedResult = new ProcessResult().setStatus(0).setOut("Hi univers");
//        when(mockProcessHelper.launchCommandProcess(dockerRunCommand)).thenReturn(expectedResult);
//
//        var result = sut.start(folderPath, dockerImage, containerName);
//
//        assertThat(result).isEqualTo(expectedResult);
//    }
}