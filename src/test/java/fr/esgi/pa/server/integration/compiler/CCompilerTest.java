package fr.esgi.pa.server.integration.compiler;

import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.infrastructure.device.CCompiler;
import fr.esgi.pa.server.common.core.utils.io.FileReader;
import fr.esgi.pa.server.common.core.utils.io.FileWriter;
import fr.esgi.pa.server.process.core.ProcessHelper;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CCompilerTest {

    @Autowired
    private FileReader fileReader;

    @Autowired
    private FileWriter fileWriter;

    @Autowired
    private ProcessHelper processHelper;

    private CCompiler sut;

    @BeforeEach
    void setup() {
        sut = new CCompiler(fileReader, fileWriter, processHelper);
    }

    @Test
    void when_content_code_correct_should_return_success_code_and_output() throws IOException, InterruptedException {
        var helloWorldContent = "#include <stdio.h>\n" +
                "int main() {\n" +
                "   // printf() displays the string inside quotation\n" +
                "   printf(\"Hello World!\");\n" +
                "   return 0;\n" +
                "}";
        var imageName = "compile_docker_test";
        var language = new Language().setId(1L).setLanguageName(LanguageName.C).setFileExtension("c");
        var result = sut.compile(helloWorldContent, language, imageName);

        var deleteImagesProcess = processHelper.createCommandProcess(new String[]{"docker", "rmi", imageName});
        if (deleteImagesProcess.waitFor() != 0) {
            System.err.println("Problem delete image '" + imageName + "'");
        }

        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.SUCCESS);
        assertThat(result.getOutput()).isEqualTo("Hello World!");
    }

    @Test
    void when_content_not_correct_should_return_fail_code_with_compilation_error_status() throws IOException, InterruptedException {
        var helloWorldContent = "#include <stdio.h>\n" +
                "int main() {\n" +
                "   // printf() displays the string inside quotation\n" +
                "   printf(\"Hello World!\")\n" +
                "   return 0;\n" +
                "}";
        var imageName = "compile_docker_test";
        var language = new Language().setId(1L).setLanguageName(LanguageName.C).setFileExtension("c");
        var result = sut.compile(helloWorldContent, language, imageName);

        var deleteImagesProcess = processHelper.createCommandProcess(new String[]{"docker", "rmi", imageName});
        if (deleteImagesProcess.waitFor() != 0) {
            System.err.println("Problem delete image '" + imageName + "'");
        }

        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.COMPILATION_ERROR);
        assertThat(result.getOutput()).isNotEqualTo("Hello World!");
    }

    @Disabled
    @Test
    void when_content_code_infinite_loop_should_return_fail_code_with_time_limit_error() throws IOException, InterruptedException {
        var helloWorldContent = "#include <stdio.h>\n" +
                "int main() {\n" +
                "   // printf() displays the string inside quotation\n" +
                "   while(1){}\n" +
                "   return 0;\n" +
                "}";
        var imageName = "compile_docker_test";
        var language = new Language().setId(1L).setLanguageName(LanguageName.C).setFileExtension("c");
        var result = sut.compile(helloWorldContent, language, imageName);

        var deleteImagesProcess = processHelper.createCommandProcess(new String[]{"docker", "rmi", imageName});
        if (deleteImagesProcess.waitFor() != 0) {
            System.err.println("Problem delete image '" + imageName + "'");
        }

        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.TIME_LIMIT_EXCEED);
    }

    @Test
    void when_content_code_not_success_should_return_fail_code_with_runtime_error() throws IOException, InterruptedException {
        var helloWorldContent = "#include <stdio.h>\n" +
                "int main() {\n" +
                "   // printf() displays the string inside quotation\n" +
                "   return 1;\n" +
                "}";
        var imageName = "compile_docker_test";
        var language = new Language().setId(1L).setLanguageName(LanguageName.C).setFileExtension("c");
        var result = sut.compile(helloWorldContent, language, imageName);

        var deleteImagesProcess = processHelper.createCommandProcess(new String[]{"docker", "rmi", imageName});
        if (deleteImagesProcess.waitFor() != 0) {
            System.err.println("Problem delete image '" + imageName + "'");
        }

        assertThat(result).isNotNull();
        assertThat(result.getCodeState()).isEqualTo(CodeState.RUNTIME_ERROR);
    }
}