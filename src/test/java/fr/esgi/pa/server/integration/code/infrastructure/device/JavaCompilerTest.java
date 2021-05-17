package fr.esgi.pa.server.integration.code.infrastructure.device;

import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.infrastructure.device.JavaCompiler;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JavaCompilerTest {

    @Autowired
    private JavaCompiler sut;
    private final String imageName = "compile_docker_test";
    private final String containerName = "containerName";

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
        var result = sut.compile(helloWorldContent, language, imageName, containerName);
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
        var result = sut.compile(helloWorldContent, language, imageName, this.containerName);
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
        var result = sut.compile(helloWorldContent, language, imageName, this.containerName);
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
        var result = sut.compile(helloWorldContent, language, imageName, this.containerName);
        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.RUNTIME_ERROR);
    }
}