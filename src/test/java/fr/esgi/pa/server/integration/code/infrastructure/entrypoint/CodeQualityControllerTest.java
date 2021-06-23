package fr.esgi.pa.server.integration.code.infrastructure.entrypoint;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CodeQualityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("POST /api/code-quality")
    class PostCodeQuality {
        @Test
        void when_user_not_authenticate_should_send_unauthorized_response() throws Exception {
            mockMvc.perform(post("/api/code-quality"))
                    .andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @ParameterizedTest
        @ValueSource(strings = {"", " ", "\n", "\t", "notnumber", "0"})
        void when_userId_attribute_is_not_correct_should_return_bad_request_error_response(String incorrectUserId) throws Exception {
            mockMvc.perform(post("/api/code-quality")
                    .requestAttr("userId", incorrectUserId))
                    .andExpect(status().isBadRequest());
        }

    }
}
