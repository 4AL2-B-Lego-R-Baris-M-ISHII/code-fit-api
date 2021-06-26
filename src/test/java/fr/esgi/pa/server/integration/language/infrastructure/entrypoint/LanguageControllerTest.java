package fr.esgi.pa.server.integration.language.infrastructure.entrypoint;

import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.language.usecase.FindAllLanguages;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static fr.esgi.pa.server.helper.JsonHelper.jsonToObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LanguageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FindAllLanguages mockFindAllLanguages;

    @Nested
    @DisplayName("get /api/language")
    class GetAll {

        @Test
        void when_user_not_authorized_should_response_error_unauthorized() throws Exception {
            mockMvc.perform(get("/api/language"))
                    .andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @Test
        void should_call_usecase_to_get_all_languages() throws Exception {
            mockMvc.perform(get("/api/language"));

            verify(mockFindAllLanguages, times(1)).execute();
        }

        @WithMockUser
        @Test
        void when_use_case_findAllLanguages_call_and_response_success_should_return_set_of_languages() throws Exception {
            var setLanguages = Set.of(
                    new Language().setLanguageName(LanguageName.C11).setFileExtension("c"),
                    new Language().setLanguageName(LanguageName.JAVA8).setFileExtension("java")
            );
            when(mockFindAllLanguages.execute()).thenReturn(setLanguages);

            var contentAsString = mockMvc.perform(get("/api/language"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            var responseArray = jsonToObject(contentAsString, Language[].class);
            Set<Language> response = new HashSet<>(Arrays.asList(responseArray));
            assertThat(response).isEqualTo(setLanguages);
        }
    }
}