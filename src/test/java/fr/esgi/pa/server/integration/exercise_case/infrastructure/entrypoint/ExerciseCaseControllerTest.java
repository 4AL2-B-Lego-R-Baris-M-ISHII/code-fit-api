package fr.esgi.pa.server.integration.exercise_case.infrastructure.entrypoint;

import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.common.core.exception.AlreadyCreatedException;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.request.SaveExerciseCaseRequest;
import fr.esgi.pa.server.exercise_case.usecase.CreateExerciseCase;
import fr.esgi.pa.server.exercise_case.usecase.DeleteOneExerciseCase;
import fr.esgi.pa.server.exercise_case.usecase.GetAllExerciseCaseByUserId;
import fr.esgi.pa.server.exercise_case.usecase.GetOneExerciseCase;
import fr.esgi.pa.server.helper.JsonHelper;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static fr.esgi.pa.server.helper.JsonHelper.jsonToObject;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExerciseCaseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateExerciseCase mockCreateExerciseCase;

    @MockBean
    private GetOneExerciseCase mockGetOneExerciseCase;

    @MockBean
    private DeleteOneExerciseCase mockDeleteOneExerciseCase;

    @MockBean
    private GetAllExerciseCaseByUserId mockGetAllExerciseCaseByUserId;

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

    @DisplayName("GET /api/exercise-case/{id}")
    @Nested
    class GetOneByIdExerciseCase {
        @Test
        void when_user_not_authenticate_should_send_unauthorized_error_response() throws Exception {
            mockMvc.perform(
                    get("/api/exercise-case/45")
            ).andExpect(status().isUnauthorized());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "USER")
        @ParameterizedTest
        @ValueSource(strings = {"notnumber", "0", "1.5", "-8", "\n", "\t", "  "})
        void when_request_path_usecase_id_is_not_correct_should_send_bad_request_error_response(String incorrectUserId) throws Exception {
            mockMvc.perform(
                    get("/api/exercise-case/123")
                            .requestAttr("userId", incorrectUserId)
            ).andExpect(status().isBadRequest());
        }


        @WithMockUser(username = "toto", password = "toto", roles = "USER")
        @ParameterizedTest
        @ValueSource(strings = {"notnumber", "0", "1.5", "-8"})
        void when_request_path_exercise_case_id_is_not_correct_should_send_bad_request_error_response(String incorrectExerciseCaseId) throws Exception {
            mockMvc.perform(
                    get("/api/exercise-case/" + incorrectExerciseCaseId)
                            .requestAttr("userId", "1")
            ).andExpect(status().isBadRequest());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "USER")
        @Test
        void when_exercise_and_user_ids_are_correct_should_call_getOneExerciseCase_usecase() throws Exception {
            mockMvc.perform(
                    get("/api/exercise-case/123")
                            .requestAttr("userId", "123")
            ).andExpect(status().isOk());

            verify(mockGetOneExerciseCase, times(1)).execute(123L, 123L);
        }

        @WithMockUser(username = "toto", password = "toto", roles = "USER")
        @Test
        void when_getOneExerciseCase_throw_NotFoundException_should_send_not_found_error_response() throws Exception {
            when(mockGetOneExerciseCase.execute(1L, 123L)).thenThrow(new NotFoundException("toto"));

            mockMvc.perform(
                    get("/api/exercise-case/123")
                            .requestAttr("userId", "1")
            ).andExpect(status().isNotFound());

        }

        @WithMockUser(username = "toto", password = "toto", roles = "USER")
        @Test
        void when_getOneExerciseCase_return_dto_exercise_case_should_send_success_response_and_returned_dto() throws Exception {
            var expectedDto = new DtoExerciseCase()
                    .setId(123L)
                    .setLanguage(new Language().setId(3L).setLanguageName(LanguageName.JAVA8).setFileExtension("java"))
                    .setTests(Set.of(
                            new DtoExerciseTest().setId(2L).setContent("test content")
                    ))
                    .setSolution("solution")
                    .setIsValid(false)
                    .setStartContent("start content");
            when(mockGetOneExerciseCase.execute(1L, 123L)).thenReturn(expectedDto);

            var contentAsString = mockMvc.perform(
                    get("/api/exercise-case/123")
                            .requestAttr("userId", "1")
            ).andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            assertThat(contentAsString).isNotNull();
            assertThat(contentAsString).isNotBlank();
            var response = JsonHelper.jsonToObject(contentAsString, DtoExerciseCase.class);
            assertThat(response).isEqualTo(expectedDto);
        }
    }

    @DisplayName("DELETE /api/exercise-case/{id}")
    @Nested
    class DeleteOneByExerciseCase {
        @Test
        void when_user_not_authorized_should_send_unauthorized_error_response() throws Exception {
            mockMvc.perform(
                    delete("/api/exercise-case/45")
            ).andExpect(status().isUnauthorized());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "USER")
        @Test
        void when_user_not_admin_should_send_forbidden_error_response() throws Exception {
            mockMvc.perform(
                    delete("/api/exercise-case/45")
            ).andExpect(status().isForbidden());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @ParameterizedTest
        @ValueSource(strings = {"0", "2.1", "notnumber", "-1"})
        void when_exercise_case_id_not_correct_should_send_bad_request_error_response(String incorrectExerciseCaseId) throws Exception {
            mockMvc.perform(
                    delete("/api/exercise-case/" + incorrectExerciseCaseId)
            ).andExpect(status().isBadRequest());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @Test
        void when_exercise_case_id_is_correct_should_call_usecase_deleteOneExerciseCase() throws Exception {
            mockMvc.perform(
                    delete("/api/exercise-case/123")
            ).andExpect(status().isNoContent());

            verify(mockDeleteOneExerciseCase, times(1)).execute(123L);
        }
    }

    @DisplayName("GET /api/exercise-case/logged-user")
    @Nested
    class GetAllByLoggedUserId {
        @Test
        void when_user_not_authorized_should_send_unauthorized_error_response() throws Exception {
            mockMvc.perform(
                    get("/api/exercise-case/logged-user")
            ).andExpect(status().isUnauthorized());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "USER")
        @Test
        void when_user_authorized_should_call_get_all_exercise_case_by_user_id() throws Exception {
            mockMvc.perform(
                    get("/api/exercise-case/logged-user")
                            .requestAttr("userId", "798")
            );

            verify(mockGetAllExerciseCaseByUserId, times(1)).execute(798L);
        }

        @WithMockUser(username = "toto", password = "toto", roles = "USER")
        @Test
        void when_get_all_exercise_case_by_user_id_should_return_set_dto_exercise_case() throws Exception {
            var language = new Language().setId(7L).setLanguageName(LanguageName.C11).setFileExtension("c");
            var setCode = Set.of(new DtoCode().setCodeId(5L).setContent("code content").setIsResolved(true));
            var setTest = Set.of(new DtoExerciseTest().setId(8L).setContent("test content"));
            var dtoExerciseCase = new DtoExerciseCase()
                    .setId(4L)
                    .setSolution("solution")
                    .setStartContent("start content")
                    .setIsValid(true)
                    .setTests(setTest)
                    .setLanguage(language)
                    .setCodes(setCode);
            var expectedValue = Set.of(dtoExerciseCase);
            when(mockGetAllExerciseCaseByUserId.execute(798L)).thenReturn(expectedValue);

            var contentAsString = mockMvc.perform(get("/api/exercise-case/logged-user")
                    .requestAttr("userId", "798"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            assertThat(contentAsString).isNotNull();
            assertThat(contentAsString).isNotBlank();
            var arrayDtoExerciseCase = jsonToObject(contentAsString, DtoExerciseCase[].class);
            assertThat(arrayDtoExerciseCase).isNotEmpty();
            var result = new HashSet<>(Arrays.asList(arrayDtoExerciseCase));
            assertThat(result).isEqualTo(expectedValue);
        }
    }
}