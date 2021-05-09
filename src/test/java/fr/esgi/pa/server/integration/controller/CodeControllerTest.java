package fr.esgi.pa.server.integration.controller;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.core.CompilationException;
import fr.esgi.pa.server.code.infrastructure.entrypoint.CodeRequest;
import fr.esgi.pa.server.code.usecase.CompileCode;
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
    private CompileCode mockCompileCode;

    @Nested
    @DisplayName("/post code")
    class CompileCodeRoute {

        @Test
        void when_user_not_authenticate_should_send_unauthorized_response() throws Exception {
            var codeRequest = new CodeRequest()
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
            var codeRequest = new CodeRequest()
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
            var codeRequest = new CodeRequest()
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
            var codeRequest = new CodeRequest()
                    .setLanguage("c")
                    .setContent("content language C");
            mockMvc.perform(post("/api/code")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonHelper.objectToJson(codeRequest)))
                    .andExpect(status().is(200));

            verify(mockCompileCode, times(1)).execute(codeRequest.getContent(), codeRequest.getLanguage());
        }

        @WithMockUser
        @Test
        void when_code_compile_should_return_code_result() throws Exception, CompilationException {
            var codeRequest = new CodeRequest()
                    .setLanguage("c")
                    .setContent("content language C");
            var expectedCode = new Code().setOutput("output").setCodeState(CodeState.SUCCESS);
            when(mockCompileCode.execute(codeRequest.getContent(), codeRequest.getLanguage())).thenReturn(expectedCode);

            var contentAsString = mockMvc.perform(post("/api/code")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonHelper.objectToJson(codeRequest)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            var response = jsonToObject(contentAsString, Code.class);

            assertThat(response).isEqualTo(expectedCode);
        }
    }
}