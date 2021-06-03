package fr.esgi.pa.server.integration.exercise.infrastructure.entrypoint;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.request.SaveExerciseRequest;
import fr.esgi.pa.server.exercise.usecase.FindOneExercise;
import fr.esgi.pa.server.exercise.usecase.SaveOneExercise;
import fr.esgi.pa.server.helper.JsonHelper;
import fr.esgi.pa.server.language.core.exception.IncorrectLanguageNameException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExerciseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SaveOneExercise mockSaveOneExercise;

    @MockBean
    private FindOneExercise mockFindOneExercise;

    @DisplayName("POST /api/exercise")
    @Nested
    class PostExerciseTest {
        @Test
        void when_user_not_authenticate_should_send_unauthorized_error_response() throws Exception {
            mockMvc.perform(
                    post("/api/exercise")
            ).andExpect(status().isUnauthorized());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "USER")
        @Test
        void when_user_not_admin_should_send_forbidden_error_response() throws Exception {
            var saveExerciseRequest = new SaveExerciseRequest()
                    .setTitle("title")
                    .setDescription("description")
                    .setLanguage("C");
            mockMvc.perform(
                    post("/api/exercise")
                            .requestAttr("userId", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveExerciseRequest)))
                    .andExpect(status().isForbidden());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @ParameterizedTest
        @ValueSource(strings = {"", "\n", "\t", "  "})
        void when_title_has_incorrect_value_should_send_bad_request_response(String incorrectTitle) throws Exception {
            var saveExerciseRequest = new SaveExerciseRequest()
                    .setTitle(incorrectTitle)
                    .setDescription("description")
                    .setLanguage("C");
            mockMvc.perform(
                    post("/api/exercise")
                            .requestAttr("userId", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveExerciseRequest)))
                    .andExpect(status().isBadRequest());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @ParameterizedTest
        @ValueSource(strings = {"", "\n", "\t", "  "})
        void when_description_has_incorrect_value_should_send_bad_request_response(String incorrectDescription) throws Exception {
            var saveExerciseRequest = new SaveExerciseRequest()
                    .setTitle("title")
                    .setDescription(incorrectDescription)
                    .setLanguage("C");
            mockMvc.perform(
                    post("/api/exercise")
                            .requestAttr("userId", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveExerciseRequest)))
                    .andExpect(status().isBadRequest());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @ParameterizedTest
        @ValueSource(strings = {"", "\n", "\t", "  "})
        void when_language_has_incorrect_value_should_send_bad_request_response(String incorrectLanguage) throws Exception {
            var saveExerciseRequest = new SaveExerciseRequest()
                    .setTitle("title")
                    .setDescription("description")
                    .setLanguage(incorrectLanguage);
            mockMvc.perform(
                    post("/api/exercise")
                            .requestAttr("userId", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveExerciseRequest)))
                    .andExpect(status().isBadRequest());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @ParameterizedTest
        @ValueSource(strings = {"", "\n", "\t", "  ", "notnumber", "-1", "2.1", "0"})
        void when_userId_has_incorrect_value_should_send_bad_request_response(String incorrectUserId) throws Exception {
            var saveExerciseRequest = new SaveExerciseRequest()
                    .setTitle("title")
                    .setDescription("description")
                    .setLanguage("c");
            mockMvc.perform(
                    post("/api/exercise")
                            .requestAttr("userId", incorrectUserId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveExerciseRequest)))
                    .andExpect(status().isBadRequest());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @Test
        void when_request_send_by_admin_and_body_request_correct_should_save_one_exercise() throws Exception {
            var saveExerciseRequest = new SaveExerciseRequest()
                    .setTitle("title")
                    .setDescription("description")
                    .setLanguage("C");
            mockMvc.perform(
                    post("/api/exercise")
                            .requestAttr("userId", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveExerciseRequest)))
                    .andExpect(status().isCreated());

            verify(mockSaveOneExercise, times(1)).execute(
                    "title",
                    "description",
                    "C",
                    1L
            );
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @Test
        void when_usecase_throw_notFoundException_should_send_not_found_status_error_response() throws Exception {
            var saveExerciseRequest = new SaveExerciseRequest()
                    .setTitle("title")
                    .setDescription("description")
                    .setLanguage("C");
            when(mockSaveOneExercise.execute(
                    "title",
                    "description",
                    "C",
                    1L
            )).thenThrow(new NotFoundException("not found"));
            var content = mockMvc.perform(
                    post("/api/exercise")
                            .requestAttr("userId", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveExerciseRequest)))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isEqualTo("not found");
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @Test
        void when_usecase_throw_incorrectLanguageNameException_should_send_not_found_status_error_response() throws Exception {
            var saveExerciseRequest = new SaveExerciseRequest()
                    .setTitle("title")
                    .setDescription("description")
                    .setLanguage("C");
            when(mockSaveOneExercise.execute(
                    "title",
                    "description",
                    "C",
                    1L
            )).thenThrow(new IncorrectLanguageNameException("language not found"));
            var content = mockMvc.perform(
                    post("/api/exercise")
                            .requestAttr("userId", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveExerciseRequest)))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isEqualTo("language not found");
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @Test
        void when_save_one_exercise_success_should_response_concerned_uri() throws Exception {
            var saveExerciseRequest = new SaveExerciseRequest()
                    .setTitle("title")
                    .setDescription("description")
                    .setLanguage("C");
            when(mockSaveOneExercise.execute(
                    "title",
                    "description",
                    "C",
                    1L
            )).thenReturn(56L);
            var location = mockMvc.perform(
                    post("/api/exercise")
                            .requestAttr("userId", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(saveExerciseRequest)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getHeader("Location");

            var response = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/api/exercise/{id}")
                    .buildAndExpand("56")
                    .toUri();
            assertThat(location).isEqualTo(response.toString());
        }
    }

    @DisplayName("GET /api/exercise/{id}")
    @Nested
    class GetOneExerciseTest {

        @Test
        void when_user_is_not_authenticate_should_send_unauthorized_error_response() throws Exception {
            var userId = 7L;
            var exerciseId = 8L;
            mockMvc.perform(
                    get("/api/exercise/" + exerciseId)
                            .requestAttr("userId", userId)
            ).andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @ParameterizedTest
        @ValueSource(strings = {"notnumber", "3.1", "-1", "0", "\t", "\n"})
        void when_userId_is_not_number_should_send_forbidden_error_response(String notCorrectUserId) throws Exception {
            var exerciseId = 8L;
            mockMvc.perform(
                    get("/api/exercise/" + exerciseId)
                            .requestAttr("userId", notCorrectUserId)
            ).andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }

        @WithMockUser
        @ParameterizedTest
        @ValueSource(strings = {"notnumber", "3.1", "-1", "0"})
        void when_exerciseId_is_not_number_should_send_forbidden_error_response(String notCorrectExerciseId) throws Exception {
            var userId = 8L;
            mockMvc.perform(
                    get("/api/exercise/" + notCorrectExerciseId)
                            .requestAttr("userId", userId)
            ).andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }

        @WithMockUser
        @Test
        void when_user_is_authenticate_should_call_usecase_findOneExercise() throws Exception {
            var userId = 7L;
            var exerciseId = 8L;
            mockMvc.perform(
                    get("/api/exercise/" + exerciseId)
                            .requestAttr("userId", userId)
            ).andExpect(status().isOk());

            verify(mockFindOneExercise, times(1)).execute(exerciseId, userId);
        }

        // TODO : continue after usecase find one exercise done
//        @WithMockUser
//        @Test
//        void when_usecase_findOneExercise_return_exercise_should_send_success_response_with_exercise() throws Exception {
//            var userId = 7L;
//            var exerciseId = 8L;
//            var expectedExercise = new Exercise()
//                    .setId(8L)
//                    .setTitle("title")
//                    .setDescription("description")
//                    .setSolution("solution");
//            when(mockFindOneExercise.execute(exerciseId, userId)).thenReturn(expectedExercise);
//            var contentAsString = mockMvc.perform(
//                    get("/api/exercise/" + exerciseId)
//                            .requestAttr("userId", userId)
//            ).andExpect(status().isOk())
//                    .andReturn()
//                    .getResponse()
//                    .getContentAsString();
//            assertThat(contentAsString).isNotNull();
//            assertThat(contentAsString).isNotBlank();
//            var response = JsonHelper.jsonToObject(contentAsString, Exercise.class);
//            assertThat(response).isEqualTo(expectedExercise);
//        }

        @WithMockUser
        @Test
        void when_usecase_findOneExercise_throw_not_found_exception_should_send_not_found_error_response() throws Exception {
            var userId = 7L;
            var exerciseId = 8L;
            when(mockFindOneExercise.execute(exerciseId, userId)).thenThrow(new NotFoundException("not found"));
            mockMvc.perform(
                    get("/api/exercise/" + exerciseId)
                            .requestAttr("userId", userId)
            ).andExpect(status().isNotFound());
        }
    }
}