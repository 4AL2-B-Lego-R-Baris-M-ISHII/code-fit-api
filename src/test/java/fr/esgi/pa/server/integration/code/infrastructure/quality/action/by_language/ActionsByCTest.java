package fr.esgi.pa.server.integration.code.infrastructure.quality.action.by_language;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.ActionsByC;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ActionsByCTest {
    @Autowired
    private ActionsByC sut;

    @Nested
    class GetNbLinesCodeTest {
        @Test
        void when_content_contain_zero_line_break_should_return_1() {
            var content = "int main(){return0;};";

            var result = sut.getNbLinesCode(content);

            assertThat(result).isEqualTo(1L);
        }

        @Test
        void when_content_contain_one_linux_line_break_should_return_2() {
            var content = "int main(){\nreturn0;};";

            var result = sut.getNbLinesCode(content);

            assertThat(result).isEqualTo(2L);
        }

        @Test
        void when_content_contain_one_windows_line_break_should_return_2() {
            var content = "int main(){\r\nreturn0;};";

            var result = sut.getNbLinesCode(content);

            assertThat(result).isEqualTo(2L);
        }

        @Test
        void when_content_contain_one_mac_line_break_should_return_2() {
            var content = "int main(){\rreturn0;};";

            var result = sut.getNbLinesCode(content);

            assertThat(result).isEqualTo(2L);
        }

        @Test
        void when_content_contain_3_linux_line_break_should_return_4() {
            var content = "int main(){\n\n\nreturn0;};";

            var result = sut.getNbLinesCode(content);

            assertThat(result).isEqualTo(4L);
        }
    }
}