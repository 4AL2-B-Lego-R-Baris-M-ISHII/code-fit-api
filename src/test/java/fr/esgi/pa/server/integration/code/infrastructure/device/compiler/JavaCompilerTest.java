package fr.esgi.pa.server.integration.code.infrastructure.device.compiler;

import fr.esgi.pa.server.code.core.compiler.CodeState;
import fr.esgi.pa.server.code.infrastructure.device.compiler.JavaCompiler;
import fr.esgi.pa.server.common.core.utils.process.ProcessHelper;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JavaCompilerTest {

    @Autowired
    private ProcessHelper processHelper;

    @Autowired
    private JavaCompiler sut;

    @AfterAll
    void afterAll() throws InterruptedException, IOException {
        String containerName = "code_container_java";
        var deleteContainerProcess = processHelper.launchCommandAndGetProcess(new String[]{"docker", "container", "rm", containerName});
        if (deleteContainerProcess.waitFor() != 0) {
            System.err.println("Problem delete container '" + containerName + "'");
        }
        String imageName = "code_image_java";
        var deleteImagesProcess = processHelper.launchCommandAndGetProcess(new String[]{"docker", "rmi", imageName});
        if (deleteImagesProcess.waitFor() != 0) {
            System.err.println("Problem delete image '" + imageName + "'");
        }
    }

    @Test
    void when_content_code_correct_should_return_success_code_and_output() {
        var helloWorldContent = "public class main {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello World\");\n" +
                "    }\n" +
                "}";
        var language = new Language()
                .setId(2L)
                .setLanguageName(LanguageName.JAVA)
                .setFileExtension("java");
        var result = sut.compile(helloWorldContent, language);

        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.SUCCESS);
        assertThat(result.getOutput().trim()).isEqualTo("Hello World");
    }

    @Test
    void when_content_not_correct_should_return_fail_code_with_compilation_error_status() {
        var helloWorldContent = "public class main {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello World\";\n" +
                "    }\n" +
                "}";
        var language = new Language()
                .setId(2L)
                .setLanguageName(LanguageName.JAVA)
                .setFileExtension("java");
        var result = sut.compile(helloWorldContent, language);
        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.COMPILATION_ERROR);
        assertThat(result.getOutput().trim()).isNotEqualTo("Hello World!");
    }

    @Test
    void when_content_code_throw_exception_should_return_fail_code_with_runtime_error() {
        var helloWorldContent = "public class main {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        String toto = null;" +
                "        int len = toto.length();" +
                "    }\n" +
                "}";
        var language = new Language()
                .setId(2L)
                .setLanguageName(LanguageName.JAVA)
                .setFileExtension("java");
        var result = sut.compile(helloWorldContent, language);
        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.RUNTIME_ERROR);
    }

    @Test
    void when_content_code_success_and_print_error_() {
        var helloWorldContent = "public class main {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        String toto = null;" +
                "        int len = toto.length();" +
                "    }\n" +
                "}";
        var language = new Language()
                .setId(2L)
                .setLanguageName(LanguageName.JAVA)
                .setFileExtension("java");
        var result = sut.compile(helloWorldContent, language);
        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.RUNTIME_ERROR);
    }
}