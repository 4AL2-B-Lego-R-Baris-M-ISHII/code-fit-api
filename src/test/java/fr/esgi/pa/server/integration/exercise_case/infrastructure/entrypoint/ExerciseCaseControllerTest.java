package fr.esgi.pa.server.integration.exercise_case.infrastructure.entrypoint;

import fr.esgi.pa.server.common.core.exception.AlreadyCreatedException;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.request.SaveExerciseCaseRequest;
import fr.esgi.pa.server.exercise_case.usecase.CreateExerciseCase;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExerciseCaseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateExerciseCase mockCreateExerciseCase;

    @DisplayName("POST /api/exercise-case")
    @Nested
    class PostExerciseCaseTest {

        private final long exerciseId = 9L;
        private final long languageId = 2L;

        @Test
        void when_user_not_authenticate_should_send_unauthorized_error_response() throws Exception {
            mockMvc.perform(
                    post("/api/exercise-case")
            ).andExpect(status().isUnauthorized());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "USER")
        @Test
        void when_user_not_admin_should_send_forbidden_error_response() throws Exception {
            var request = new SaveExerciseCaseRequest()
                    .setExerciseId(exerciseId)
                    .setLanguageId(languageId);
            mockMvc.perform(
                    post("/api/exercise-case")
                            .requestAttr("userId", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(request)))
                    .andExpect(status().isForbidden());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @ParameterizedTest
        @ValueSource(longs = {-1L, 0L, -56L})
        void when_request_exercise_id_is_not_correct_should_send_bad_request_error_response(Long incorrectExerciseId) throws Exception {
            var request = new SaveExerciseCaseRequest()
                    .setExerciseId(incorrectExerciseId)
                    .setLanguageId(languageId);
            mockMvc.perform(
                    post("/api/exercise-case")
                            .requestAttr("userId", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(request)))
                    .andExpect(status().isBadRequest());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @ParameterizedTest
        @ValueSource(longs = {-1L, 0L, -56L})
        void when_request_language_id_is_not_correct_should_send_bad_request_error_response(Long incorrectExerciseId) throws Exception {
            var request = new SaveExerciseCaseRequest()
                    .setExerciseId(incorrectExerciseId)
                    .setLanguageId(languageId);
            mockMvc.perform(
                    post("/api/exercise-case")
                            .requestAttr("userId", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(request)))
                    .andExpect(status().isBadRequest());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @ParameterizedTest
        @ValueSource(strings = {"notnumber", "-1", "0", "\n", "\t", "4.5"})
        void when_request_user_id_is_not_correct_should_send_bad_request_error_response(String incorrectUserId) throws Exception {
            var request = new SaveExerciseCaseRequest()
                    .setExerciseId(exerciseId)
                    .setLanguageId(languageId);
            mockMvc.perform(
                    post("/api/exercise-case")
                            .requestAttr("userId", incorrectUserId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(request)))
                    .andExpect(status().isBadRequest());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @Test
        void when_request_correct_should_call_usecase_to_create_exercise_case() throws Exception {
            var request = new SaveExerciseCaseRequest()
                    .setExerciseId(exerciseId)
                    .setLanguageId(languageId);
            mockMvc.perform(
                    post("/api/exercise-case")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(request)));

            verify(mockCreateExerciseCase, times(1)).execute(3L, exerciseId, languageId);
        }

        // TODO : continue after use case done
        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @Test
        void when_use_case_throw_NotFoundException_should_send_not_found_error_response() throws Exception {
            var request = new SaveExerciseCaseRequest()
                    .setExerciseId(exerciseId)
                    .setLanguageId(languageId);
            when(mockCreateExerciseCase.execute(3L, exerciseId, languageId))
                    .thenThrow(new NotFoundException("not found exception thrown"));
            var content = mockMvc.perform(
                    post("/api/exercise-case")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(request)))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            assertThat(content).isEqualTo("not found exception thrown");
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @Test
        void when_use_case_throw_ForbiddenException_should_send_forbidden_error_response() throws Exception {
            var request = new SaveExerciseCaseRequest()
                    .setExerciseId(exerciseId)
                    .setLanguageId(languageId);
            when(mockCreateExerciseCase.execute(3L, exerciseId, languageId))
                    .thenThrow(new ForbiddenException("forbidden exception thrown"));
            var content = mockMvc.perform(
                    post("/api/exercise-case")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(request)))
                    .andExpect(status().isForbidden())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            assertThat(content).isEqualTo("forbidden exception thrown");
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @Test
        void when_use_case_throw_AlreadyCreatedException_should_send_forbidden_error_response() throws Exception {
            var request = new SaveExerciseCaseRequest()
                    .setExerciseId(exerciseId)
                    .setLanguageId(languageId);
            when(mockCreateExerciseCase.execute(3L, exerciseId, languageId))
                    .thenThrow(new AlreadyCreatedException("already created exception thrown"));
            var content = mockMvc.perform(
                    post("/api/exercise-case")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(request)))
                    .andExpect(status().isForbidden())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            assertThat(content).isEqualTo("already created exception thrown");
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @Test
        void when_use_case_return_id_of_new_exercise_case_should_send_success_response_with_uri() throws Exception {
            var request = new SaveExerciseCaseRequest()
                    .setExerciseId(exerciseId)
                    .setLanguageId(languageId);
            when(mockCreateExerciseCase.execute(3L, exerciseId, languageId))
                    .thenReturn(456L);
            var location = mockMvc.perform(
                    post("/api/exercise-case")
                            .requestAttr("userId", "3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(request)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getHeader("Location");

            var expectedURI = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/api/exercise-case/{id}")
                    .buildAndExpand("456")
                    .toUri();
            assertThat(location).isEqualTo(expectedURI.toString());
        }
    }
}