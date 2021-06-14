package fr.esgi.pa.server.integration.code.infrastructure.entrypoint;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.compiler.CodeState;
import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.code.core.exception.CompilationException;
import fr.esgi.pa.server.code.infrastructure.entrypoint.TestCompileCodeRequest;
import fr.esgi.pa.server.code.infrastructure.entrypoint.request.SaveCodeRequest;
import fr.esgi.pa.server.code.usecase.CompileCodeById;
import fr.esgi.pa.server.code.usecase.SaveOneCode;
import fr.esgi.pa.server.code.usecase.TestCompileCode;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;

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
    private SaveOneCode mockSaveOneCode;

    @MockBean
    private CompileCodeById mockCompileCodeById;

    @MockBean
    private TestCompileCode mockTestCompileCode;

    @Nested
    @DisplayName("/post api/code")
    class PostCode {

        private final long exerciseCaseId = 2L;
        private final String codeContent = "code content";
        private final long userId = 3L;
        private final long codeId = 7L;

        @Test
        void when_user_not_authenticate_should_send_unauthorized_error_response() throws Exception {
            mockMvc.perform(
                    post("/api/code")
            ).andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @ParameterizedTest
        @ValueSource(strings = {"", " ", "\n", "\t", "notnumber", "-1", "4.6", "0"})
        void when_userId_of_request_is_incorrect_should_send_bad_request_error_response(String incorrectUserId) throws Exception {
            var saveCodeRequest = new SaveCodeRequest()
                    .setExerciseCaseId(exerciseCaseId)
                    .setCodeContent(codeContent);
            mockMvc.perform(
                    post("/api/code")
                            .requestAttr("userId", incorrectUserId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveCodeRequest))
            ).andExpect(status().isBadRequest());
        }

        @WithMockUser
        @Test
        void when_exerciseCaseId_of_request_is_null_should_send_bad_request_error_response() throws Exception {
            var saveCodeRequest = new SaveCodeRequest()
                    .setExerciseCaseId(null)
                    .setCodeContent(codeContent);
            mockMvc.perform(
                    post("/api/code")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveCodeRequest))
            ).andExpect(status().isBadRequest());
        }

        @WithMockUser
        @ParameterizedTest
        @ValueSource(longs = {-9L, 0L})
        void when_exerciseCaseId_of_request_is_not_correct_should_send_bad_request_error_response(Long incorrectExerciseCaseId) throws Exception {
            var saveCodeRequest = new SaveCodeRequest()
                    .setExerciseCaseId(incorrectExerciseCaseId)
                    .setCodeContent(codeContent);
            mockMvc.perform(
                    post("/api/code")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveCodeRequest))
            ).andExpect(status().isBadRequest());
        }

        @WithMockUser
        @Test
        void when_content_of_request_is_null_should_send_bad_request_error_response() throws Exception {
            var saveCodeRequest = new SaveCodeRequest()
                    .setExerciseCaseId(exerciseCaseId)
                    .setCodeContent(null);
            mockMvc.perform(
                    post("/api/code")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveCodeRequest))
            ).andExpect(status().isBadRequest());
        }

        @WithMockUser
        @ParameterizedTest
        @ValueSource(strings = {"", " ", "\t", "\n"})
        void when_content_of_request_is_blank_should_send_bad_request_error_response(String blankContent) throws Exception {
            var saveCodeRequest = new SaveCodeRequest()
                    .setExerciseCaseId(exerciseCaseId)
                    .setCodeContent(blankContent);
            mockMvc.perform(
                    post("/api/code")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveCodeRequest))
            ).andExpect(status().isBadRequest());
        }

        @WithMockUser
        @Test
        void when_content_size_of_request_is_more_than_60k_should_send_bad_request_error_response() throws Exception {
            var saveCodeRequest = new SaveCodeRequest()
                    .setExerciseCaseId(exerciseCaseId)
                    .setCodeContent("a".repeat(60001));
            mockMvc.perform(
                    post("/api/code")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveCodeRequest))
            ).andExpect(status().isBadRequest());
        }

        @WithMockUser
        @Test
        void when_request_properties_are_correct_should_call_usecase_saveOneCode() throws Exception {
            var saveCodeRequest = new SaveCodeRequest()
                    .setExerciseCaseId(exerciseCaseId)
                    .setCodeContent(codeContent);
            mockMvc.perform(
                    post("/api/code")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveCodeRequest))
            );

            verify(mockSaveOneCode, times(1))
                    .execute(userId, exerciseCaseId, codeContent);
        }

        @WithMockUser
        @Test
        void when_usecase_throw_NotFoundException_should_send_not_found_error_response() throws Exception {
            var saveCodeRequest = new SaveCodeRequest()
                    .setExerciseCaseId(exerciseCaseId)
                    .setCodeContent(codeContent);
            when(mockSaveOneCode.execute(userId, exerciseCaseId, codeContent)).thenThrow(new NotFoundException("not found"));

            mockMvc.perform(
                    post("/api/code")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveCodeRequest))
            );
        }

        @WithMockUser
        @Test
        void when_request_toCompile_property_is_false_should_not_call_usecase_compile_code_by_id() throws Exception {
            var saveCodeRequest = new SaveCodeRequest()
                    .setExerciseCaseId(exerciseCaseId)
                    .setCodeContent(codeContent)
                    .setToCompile(false);
            when(mockSaveOneCode.execute(userId, exerciseCaseId, codeContent)).thenReturn(codeId);

            mockMvc.perform(
                    post("/api/code")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveCodeRequest))
            );

            verify(mockCompileCodeById, never()).execute(anyLong());
        }

        @WithMockUser
        @Test
        void when_request_toCompile_property_is_false_and_usecase_return_id_should_send_created_response_with_uri() throws Exception {
            var saveCodeRequest = new SaveCodeRequest()
                    .setExerciseCaseId(exerciseCaseId)
                    .setCodeContent(codeContent)
                    .setToCompile(false);
            when(mockSaveOneCode.execute(userId, exerciseCaseId, codeContent)).thenReturn(codeId);

            var location = mockMvc.perform(
                    post("/api/code")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveCodeRequest)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getHeader("Location");

            var expectedURI = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/api/code/{id}")
                    .buildAndExpand(codeId)
                    .toUri();
            assertThat(location).isEqualTo(expectedURI.toString());
        }

        @WithMockUser
        @Test
        void when_request_toCompile_property_is_true_should_call_usecase_compile_code_by_id() throws Exception {
            var saveCodeRequest = new SaveCodeRequest()
                    .setExerciseCaseId(exerciseCaseId)
                    .setCodeContent(codeContent)
                    .setToCompile(true);
            when(mockSaveOneCode.execute(userId, exerciseCaseId, codeContent)).thenReturn(codeId);

            mockMvc.perform(
                    post("/api/code")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveCodeRequest))
            );

            verify(mockCompileCodeById, times(1)).execute(codeId);
        }

        @WithMockUser
        @Test
        void when_request_toCompile_property_is_true_and_usecase_compileCodeById_return_dtoCode_should_send_ok_response_with_dtoCode() throws Exception {
            var saveCodeRequest = new SaveCodeRequest()
                    .setExerciseCaseId(exerciseCaseId)
                    .setCodeContent(codeContent)
                    .setToCompile(true);
            when(mockSaveOneCode.execute(userId, exerciseCaseId, codeContent)).thenReturn(codeId);
            var dtoCode = new DtoCode()
                    .setCodeId(12L)
                    .setIsResolved(true)
                    .setListCodeResult(new ArrayList<>());
            when(mockCompileCodeById.execute(codeId)).thenReturn(dtoCode);
            var contentAsString = mockMvc.perform(
                    post("/api/code")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveCodeRequest))
            ).andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            assertThat(contentAsString).isNotNull();
            assertThat(contentAsString).isNotBlank();
            var response = JsonHelper.jsonToObject(contentAsString, DtoCode.class);
            assertThat(response).isEqualTo(dtoCode);
        }
    }

    @Nested
    @DisplayName("/post api/code/test")
    class TestCompileCodeRoute {

        @Test
        void when_user_not_authenticate_should_send_unauthorized_response() throws Exception {
            var codeRequest = new TestCompileCodeRequest()
                    .setLanguage("c")
                    .setContent("correct");
            mockMvc.perform(post("/api/code/test")
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
            mockMvc.perform(post("/api/code/test")
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
            mockMvc.perform(post("/api/code/test")
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
            mockMvc.perform(post("/api/code/test")
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

            var contentAsString = mockMvc.perform(post("/api/code/test")
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