package fr.esgi.pa.server.integration.exercise.infrastructure.entrypoint;

import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.request.SaveExerciseRequest;
import fr.esgi.pa.server.exercise.infrastructure.entrypoint.request.UpdateExerciseRequest;
import fr.esgi.pa.server.exercise.usecase.*;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.helper.JsonHelper;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.language.core.exception.IncorrectLanguageNameException;
import fr.esgi.pa.server.user.core.dto.DtoUser;
import org.assertj.core.util.Sets;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockBean
    private FindAllExercises mockFindAllExercises;

    @MockBean
    private FilterExercisesByCreator mockFilterExercisesByCreator;

    @MockBean
    private AddLoggedUserCodeAllExercises mockAddLoggedUserCodeAllExercises;

    @MockBean
    private UpdateOneExercise mockUpdateOneExercise;

    @MockBean
    private DeleteOneExercise mockDeleteOneExercise;

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

        @WithMockUser
        @Test
        void when_usecase_findOneExercise_return_exercise_should_send_success_response_with_exercise() throws Exception {
            var userId = 7L;
            var exerciseId = 8L;
            var expectedExercise = new DtoExercise()
                    .setId(8L)
                    .setTitle("title")
                    .setDescription("description");

            when(mockFindOneExercise.execute(exerciseId, userId)).thenReturn(expectedExercise);
            var contentAsString = mockMvc.perform(
                    get("/api/exercise/" + exerciseId)
                            .requestAttr("userId", userId)
            ).andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            assertThat(contentAsString).isNotNull();
            assertThat(contentAsString).isNotBlank();
            var response = JsonHelper.jsonToObject(contentAsString, DtoExercise.class);
            assertThat(response).isEqualTo(expectedExercise);
        }

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

    @DisplayName("GET /api/exercise")
    @Nested
    class GetAllExercisesTest {
        @Test
        void when_user_not_authenticate_should_send_unauthorized_error_response() throws Exception {
            mockMvc.perform(
                    get("/api/exercise")
                            .requestAttr("userId", 9)
            ).andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @Test
        void when_usecase_findAllExercise_should_send_not_found_error_response() throws Exception {
            when(mockFindAllExercises.execute()).thenThrow(new NotFoundException("not found"));
            mockMvc.perform(
                    get("/api/exercise")
                            .requestAttr("userId", 9)
            ).andExpect(status().isNotFound());
        }

        @WithMockUser
        @Test
        void when_usecase_findAllExercise_return_set_dto_exercise() throws Exception {
            var setDtoExercise = Set.of(
                    new DtoExercise()
                            .setId(5L)
                            .setTitle("title")
                            .setDescription("description")
                            .setUser(new DtoUser())
                            .setCases(Set.of(
                                    new DtoExerciseCase()
                                            .setId(7L)
                                            .setLanguage(new Language().setId(8L).setFileExtension("java").setLanguageName(LanguageName.JAVA8))
                                            .setSolution("solution")
                                            .setStartContent("start content")
                                            .setIsValid(false)
                            ))
            );

            when(mockFindAllExercises.execute()).thenReturn(setDtoExercise);

            var contentAsString = mockMvc.perform(
                    get("/api/exercise")
                            .requestAttr("userId", 9)
            ).andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(contentAsString).isNotNull();
            assertThat(contentAsString).isNotBlank();

            var arrDtoExercise = JsonHelper.jsonToObject(contentAsString, DtoExercise[].class);
            assertThat(arrDtoExercise).isNotNull();
            var resultSetDtoExercise = Sets.newHashSet(Arrays.asList(arrDtoExercise));
            assertThat(resultSetDtoExercise).isEqualTo(setDtoExercise);
        }

        @WithMockUser
        @Test
        void when_usecase_findAllExercise_has_param_is_creator_should_call_usecase_filterExercisesByCreator() throws Exception {
            var setDtoExercise = Set.of(
                    new DtoExercise()
                            .setId(5L)
                            .setTitle("title")
                            .setDescription("description")
                            .setUser(new DtoUser())
                            .setCases(Set.of(
                                    new DtoExerciseCase()
                                            .setId(7L)
                                            .setLanguage(new Language().setId(8L).setFileExtension("java").setLanguageName(LanguageName.JAVA8))
                                            .setSolution("solution")
                                            .setStartContent("start content")
                                            .setIsValid(false)
                            ))
            );
            when(mockFindAllExercises.execute()).thenReturn(setDtoExercise);

            mockMvc.perform(
                    get("/api/exercise?is_creator=true")
                    .requestAttr("userId", 9)
            );
            verify(mockFilterExercisesByCreator, times(1)).execute(setDtoExercise, 9L);
        }

        @WithMockUser
        @Test
        void when_usecase_filterExercisesByCreator_should_return_filtered_set_dto_exercise() throws Exception {
            var setDtoExercise = Set.of(
                    new DtoExercise()
                            .setId(5L)
                            .setTitle("title")
                            .setDescription("description")
                            .setUser(new DtoUser())
                            .setCases(Set.of(
                                    new DtoExerciseCase()
                                            .setId(7L)
                                            .setLanguage(new Language().setId(8L).setFileExtension("java").setLanguageName(LanguageName.JAVA8))
                                            .setSolution("solution")
                                            .setStartContent("start content")
                                            .setIsValid(false)
                            )),
                    new DtoExercise()
                            .setId(6L)
                            .setTitle("title")
                            .setDescription("description")
                            .setUser(new DtoUser().setId(9L))
                            .setCases(Set.of(
                                    new DtoExerciseCase()
                                            .setId(8L)
                                            .setLanguage(new Language().setId(8L).setFileExtension("java").setLanguageName(LanguageName.JAVA8))
                                            .setSolution("solution")
                                            .setStartContent("start content")
                                            .setIsValid(false)
                            ))
            );
            when(mockFindAllExercises.execute()).thenReturn(setDtoExercise);
            var filteredSetDtoExercise = Set.of(
                    new DtoExercise()
                            .setId(6L)
                            .setTitle("title")
                            .setDescription("description")
                            .setUser(new DtoUser().setId(9L))
                            .setCases(Set.of(
                                    new DtoExerciseCase()
                                            .setId(8L)
                                            .setLanguage(new Language().setId(8L).setFileExtension("java").setLanguageName(LanguageName.JAVA8))
                                            .setSolution("solution")
                                            .setStartContent("start content")
                                            .setIsValid(false)
                            ))
            );
            when(mockFilterExercisesByCreator.execute(setDtoExercise, 9L)).thenReturn(filteredSetDtoExercise);
            var contentAsString = mockMvc.perform(
                    get("/api/exercise?is_creator=true")
                            .requestAttr("userId", 9)
            ).andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(contentAsString).isNotNull();
            assertThat(contentAsString).isNotBlank();

            var arrDtoExercise = JsonHelper.jsonToObject(contentAsString, DtoExercise[].class);
            assertThat(arrDtoExercise).isNotNull();
            var resultSetDtoExercise = Sets.newHashSet(Arrays.asList(arrDtoExercise));
            assertThat(resultSetDtoExercise).isEqualTo(filteredSetDtoExercise);
        }

        @WithMockUser
        @Test
        void when_has_request_param_logged_user_code_to_true_should_call_usecase_addLoggedUserCodeAllExercises() throws Exception {
            var setDtoExercise = Set.of(
                    new DtoExercise()
                            .setId(5L)
                            .setTitle("title")
                            .setDescription("description")
                            .setUser(new DtoUser())
                            .setCases(Set.of(
                                    new DtoExerciseCase()
                                            .setId(7L)
                                            .setLanguage(new Language().setId(8L).setFileExtension("java").setLanguageName(LanguageName.JAVA8))
                                            .setSolution("solution")
                                            .setStartContent("start content")
                                            .setIsValid(false)
                            ))
            );
            when(mockFindAllExercises.execute()).thenReturn(setDtoExercise);

            mockMvc.perform(
                    get("/api/exercise?with_logged_user_code=true")
                            .requestAttr("userId", 9)
            );

            verify(mockAddLoggedUserCodeAllExercises, times(1)).execute(setDtoExercise, 9L);
        }

        @WithMockUser
        @Test
        void when_addLoggedUserCodeAllExercises_return_set_dto_exercises_should_send_returned_result() throws Exception {
            var setDtoExercise = Set.of(
                    new DtoExercise()
                            .setId(5L)
                            .setTitle("title")
                            .setDescription("description")
                            .setUser(new DtoUser())
                            .setCases(Set.of(
                                    new DtoExerciseCase()
                                            .setId(7L)
                                            .setLanguage(new Language().setId(8L).setFileExtension("java").setLanguageName(LanguageName.JAVA8))
                                            .setSolution("solution")
                                            .setStartContent("start content")
                                            .setIsValid(false)
                            ))
            );
            when(mockFindAllExercises.execute()).thenReturn(setDtoExercise);
            var setDtoExerciseWithCodes = Set.of(
                    new DtoExercise()
                            .setId(5L)
                            .setTitle("title")
                            .setDescription("description")
                            .setUser(new DtoUser())
                            .setCases(Set.of(
                                    new DtoExerciseCase()
                                            .setId(7L)
                                            .setLanguage(new Language().setId(8L).setFileExtension("java").setLanguageName(LanguageName.JAVA8))
                                            .setSolution("solution")
                                            .setStartContent("start content")
                                            .setIsValid(false)
                                            .setCodes(
                                                    Set.of(
                                                            new DtoCode().setCodeId(6L).setContent("code content").setIsResolved(true)
                                                    )
                                            )
                            ))
            );
            when(mockAddLoggedUserCodeAllExercises.execute(setDtoExercise, 9L)).thenReturn(setDtoExerciseWithCodes);

            var contentAsString = mockMvc.perform(
                    get("/api/exercise?with_logged_user_code=true")
                            .requestAttr("userId", 9)
            ).andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(contentAsString).isNotNull();
            assertThat(contentAsString).isNotBlank();

            var arrDtoExercise = JsonHelper.jsonToObject(contentAsString, DtoExercise[].class);
            assertThat(arrDtoExercise).isNotNull();
            var resultSetDtoExercise = Sets.newHashSet(Arrays.asList(arrDtoExercise));
            assertThat(resultSetDtoExercise).isEqualTo(setDtoExerciseWithCodes);
        }
    }

    @DisplayName("PUT /api/exercise/{id}")
    @Nested
    class UpdateOneExerciseTest {
        @Test
        void when_user_not_authenticate_should_unauthorized_error_response() throws Exception {
            mockMvc.perform(
                    put("/api/exercise/23")
            ).andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @Test
        void when_user_is_not_admin_should_send_forbidden_error_response() throws Exception {
            var updateExerciseRequest = new UpdateExerciseRequest()
                    .setTitle("update title")
                    .setDescription("update description");
            mockMvc.perform(
                    put("/api/exercise/23")
                            .requestAttr("userId", "5")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(updateExerciseRequest))
            ).andExpect(status().isForbidden());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @ParameterizedTest
        @ValueSource(strings = {"notnumber", "1.2", "-1", "0"})
        void when_exercise_id_not_integer_min_1_should_send_bad_request_response(String incorrectId) throws Exception {
            var updateExerciseRequest = new UpdateExerciseRequest()
                    .setTitle("update title")
                    .setDescription("update description");
            mockMvc.perform(
                    put("/api/exercise/" + incorrectId)
                            .requestAttr("userId", "5")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(updateExerciseRequest))
            ).andExpect(status().isBadRequest());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @Test
        void when_request_correct_and_usecase_done_should_send_success_not_content_response() throws Exception {
            var updateExerciseRequest = new UpdateExerciseRequest()
                    .setTitle("update title")
                    .setDescription("update description");
            doNothing().when(mockUpdateOneExercise).execute(5L, 1L, "update title", "update description");
            mockMvc.perform(
                    put("/api/exercise/1")
                            .requestAttr("userId", "5")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonHelper.objectToJson(updateExerciseRequest))
            ).andExpect(status().isNoContent());

            verify(mockUpdateOneExercise, times(1)).execute(5L, 1L, "update title", "update description");
        }
    }

    @DisplayName("DELETE /api/exercise/{id}")
    @Nested
    class DeleteOneExerciseTest {
        @Test
        void when_user_not_authenticate_should_unauthorized_error_response() throws Exception {
            mockMvc.perform(
                    delete("/api/exercise/23")
            ).andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @Test
        void when_user_is_not_admin_should_send_forbidden_error_response() throws Exception {
            mockMvc.perform(
                    delete("/api/exercise/23")
                            .requestAttr("userId", "5")
            ).andExpect(status().isForbidden());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @ParameterizedTest
        @ValueSource(strings = {"notnumber", "1.2", "-1", "0"})
        void when_exercise_id_not_integer_min_1_should_send_bad_request_response(String incorrectId) throws Exception {
            mockMvc.perform(
                    delete("/api/exercise/" + incorrectId)
                            .requestAttr("userId", "5")
            ).andExpect(status().isBadRequest());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @ParameterizedTest
        @ValueSource(strings = {"notnumber", "1.2", "-1", "0"})
        void when_user_id_not_integer_min_1_should_send_bad_request_response(String incorrectId) throws Exception {
            mockMvc.perform(
                    delete("/api/exercise/56")
                            .requestAttr("userId", incorrectId)
            ).andExpect(status().isBadRequest());
        }

        @WithMockUser(username = "toto", password = "toto", roles = "ADMIN")
        @Test
        void when_usecase_deleteOneExercise_call_and_success_should_send_success_no_content_response() throws Exception {
            doNothing().when(mockDeleteOneExercise).execute(5L, 56L);

            mockMvc.perform(
                    delete("/api/exercise/56")
                            .requestAttr("userId", 5)
            ).andExpect(status().isNoContent());

            verify(mockDeleteOneExercise, times(1)).execute(5L, 56L);
        }
    }
}