package fr.esgi.pa.server.integration.code.infrastructure.entrypoint;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.compiler.CodeState;
import fr.esgi.pa.server.code.core.exception.CompilationException;
import fr.esgi.pa.server.code.infrastructure.entrypoint.TestCompileCodeRequest;
import fr.esgi.pa.server.code.usecase.TestCompileCode;
import fr.esgi.pa.server.helper.JsonHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static fr.esgi.pa.server.helper.JsonHelper.jsonToObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CodeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestCompileCode mockTestCompileCode;

    @Nested
    @DisplayName("/post code")
    class TestCompileCodeRoute {

        @Test
        void when_user_not_authenticate_should_send_unauthorized_response() throws Exception {
            var codeRequest = new TestCompileCodeRequest()
                    .setLanguage("c")
                    .setContent("correct");
            mockMvc.perform(post("/api/code")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonHelper.objectToJson(codeRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @ParameterizedTest
        @ValueSource(strings = {"", "  ", "\n", "\t", "oijoierjoiejrgoijeroijgoeijrgoijzoeifjoizejf"})
        void when_code_request_has_incorrect_language_name_should_send_bad_response(String incorrectLanguage) throws Exception {
            var codeRequest = new TestCompileCodeRequest()
                    .setLanguage(incorrectLanguage)
                    .setContent("correct");
            mockMvc.perform(post("/api/code")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonHelper.objectToJson(codeRequest)))
                    .andExpect(status().isBadRequest());
        }

        @WithMockUser
        @ParameterizedTest
        @ValueSource(strings = {"", "  ", "\n", "\t"})
        void when_code_request_has_incorrect_content_should_send_bad_request(String incorrectContent) throws Exception {
            var codeRequest = new TestCompileCodeRequest()
                    .setLanguage("c")
                    .setContent(incorrectContent);
            mockMvc.perform(post("/api/code")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonHelper.objectToJson(codeRequest)))
                    .andExpect(status().isBadRequest());
        }

        @WithMockUser
        @Test
        void when_code_request_correct_should_compile_code() throws Exception, CompilationException {
            var codeRequest = new TestCompileCodeRequest()
                    .setLanguage("c")
                    .setContent("content language C");
            mockMvc.perform(post("/api/code")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonHelper.objectToJson(codeRequest)))
                    .andExpect(status().is(200));

            verify(mockTestCompileCode, times(1)).execute(codeRequest.getContent(), codeRequest.getLanguage());
        }

        @WithMockUser
        @Test
        void when_code_compile_should_return_code_result() throws Exception, CompilationException {
            var codeRequest = new TestCompileCodeRequest()
                    .setLanguage("c")
                    .setContent("content language C");
            var expectedCode = new CodeResult().setOutput("output").setCodeState(CodeState.SUCCESS);
            when(mockTestCompileCode.execute(codeRequest.getContent(), codeRequest.getLanguage())).thenReturn(expectedCode);

            var contentAsString = mockMvc.perform(post("/api/code")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonHelper.objectToJson(codeRequest)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            var response = jsonToObject(contentAsString, CodeResult.class);

            assertThat(response).isEqualTo(expectedCode);
        }
    }
}