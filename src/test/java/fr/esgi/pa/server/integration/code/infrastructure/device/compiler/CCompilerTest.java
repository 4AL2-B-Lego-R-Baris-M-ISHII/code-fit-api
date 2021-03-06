package fr.esgi.pa.server.integration.code.infrastructure.device.compiler;

import fr.esgi.pa.server.code.core.compiler.CodeState;
import fr.esgi.pa.server.code.infrastructure.device.compiler.CCompiler;
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
class CCompilerTest {

    @Autowired
    private ProcessHelper processHelper;

    @Autowired
    private CCompiler sut;

    @AfterAll
    void afterAll() throws InterruptedException, IOException {
        var deleteContainerProcess = processHelper.launchCommandAndGetProcess(new String[]{"docker", "container", "rm", "code_container_c"});
        if (deleteContainerProcess.waitFor() != 0) {
            System.err.println("Problem delete container '" + "code_container_c" + "'");
        }
        var deleteImagesProcess = processHelper.launchCommandAndGetProcess(new String[]{"docker", "rmi", "code_image_c"});
        if (deleteImagesProcess.waitFor() != 0) {
            System.err.println("Problem delete image '" + "code_image_c" + "'");
        }
    }

    @Test
    void when_content_code_correct_should_return_success_code_and_output() {
        var helloWorldContent = "#include <stdio.h>\n" +
                "int main() {\n" +
                "   // printf() displays the string inside quotation\n" +
                "   printf(\"Hello World!\");\n" +
                "   return 0;\n" +
                "}";
        var language = new Language().setId(1L).setLanguageName(LanguageName.C11).setFileExtension("c");
        var result = sut.compile(helloWorldContent, language);

        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.SUCCESS);
        assertThat(result.getOutput()).isEqualTo("Hello World!");
    }

    @Test
    void when_content_not_correct_should_return_fail_code_with_compilation_error_status() {
        var helloWorldContent = "#include <stdio.h>\n" +
                "int main() {\n" +
                "   // printf() displays the string inside quotation\n" +
                "   printf(\"Hello World!\")\n" +
                "   return 0;\n" +
                "}";
        var language = new Language().setId(1L).setLanguageName(LanguageName.C11).setFileExtension("c");
        var result = sut.compile(helloWorldContent, language);

        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.COMPILATION_ERROR);
        assertThat(result.getOutput()).isNotEqualTo("Hello World!");
    }

    @Test
    void when_content_code_infinite_loop_should_return_fail_code_with_time_limit_error() {
        var helloWorldContent = "#include <stdio.h>\n" +
                "int main() {\n" +
                "   // printf() displays the string inside quotation\n" +
                "   while(1){}\n" +
                "   return 0;\n" +
                "}";
        var language = new Language().setId(1L).setLanguageName(LanguageName.C11).setFileExtension("c");
        var result = sut.compile(helloWorldContent, language);

        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.TIME_LIMIT_EXCEED);
    }

    @Test
    void when_content_code_not_success_should_return_fail_code_with_runtime_error() {
        var helloWorldContent = "#include <stdio.h>\n" +
                "int main() {\n" +
                "   // printf() displays the string inside quotation\n" +
                "   return 1;\n" +
                "}";
        var language = new Language().setId(1L).setLanguageName(LanguageName.C11).setFileExtension("c");
        var result = sut.compile(helloWorldContent, language);

        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.RUNTIME_ERROR);
    }

    @Test
    void when_content_code_success_and_print_Error_no_such_container_should_return_success_code() {
        var helloWorldContent = "#include <stdio.h>\n" +
                "int main() {\n" +
                "   // printf() displays the string inside quotation\n" +
                "   printf(\"Error: No such container\");\n" +
                "   return 0;\n" +
                "}";
        var language = new Language().setId(1L).setLanguageName(LanguageName.C11).setFileExtension("c");
        var result = sut.compile(helloWorldContent, language);

        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.SUCCESS);
        assertThat(result.getOutput()).isEqualTo("Error: No such container");
    }
}