package fr.esgi.pa.server.integration.controller;

import fr.esgi.pa.server.core.exception.AlreadyCreatedException;
import fr.esgi.pa.server.core.exception.NotFoundException;
import fr.esgi.pa.server.helper.JsonHelper;
import fr.esgi.pa.server.infrastructure.entrypoint.request.SignUpRequest;
import fr.esgi.pa.server.usecase.auth.SignUp;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignUp mockSignUp;

    @Nested
    class RegisterUser {
        @ParameterizedTest
        @ValueSource(strings = {"", "  ", "\n", "\t", "tooloooooooooooooooooooongs"})
        public void when_username_has_wrong_value_should_send_error(String notCorrectUsername) throws Exception {
            var signUpRequest = new SignUpRequest()
                    .setEmail("toto@tata.fr")
                    .setUsername(notCorrectUsername)
                    .setPassword("totopassword");
            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonHelper.objectToJson(signUpRequest)))
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "  ", "\n", "\t", "notemail", "toooooooooooooooooooooolooooooooooooooooooooooooooong@gmail.com"})
        public void when_email_not_correct_should_send_error(String notCorrectEmail) throws Exception {
            var signUpRequest = new SignUpRequest()
                    .setUsername("user name")
                    .setEmail(notCorrectEmail)
                    .setPassword("apassword");
            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonHelper.objectToJson(signUpRequest)))
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "  ", "\n", "\t", "tooooooooooooooooooooooooooooooooooooooooooloooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong"})
        public void when_password_not_correct_should_send_error(String notCorrectPassword) throws Exception {
            var signUpRequest = new SignUpRequest()
                    .setUsername("user name")
                    .setEmail("user@gmail.com")
                    .setPassword(notCorrectPassword);

            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonHelper.objectToJson(signUpRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void when_user_with_username_already_exists_should_send_error() throws Exception {
            var userName = "user name";
            var email = "user@gmail.com";
            var userPassword = "userPassword";
            when(mockSignUp.execute(userName, email, userPassword, null))
                    .thenThrow(new AlreadyCreatedException("already created user name"));
            var signUpRequest = new SignUpRequest()
                    .setUsername(userName)
                    .setEmail(email)
                    .setPassword(userPassword);

            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonHelper.objectToJson(signUpRequest)))
                    .andExpect(status().isForbidden());
        }

        @Test
        public void when_request_correct_should_send_success() throws Exception {
            var signUpRequest = new SignUpRequest()
                    .setUsername("user name")
                    .setEmail("user@gmail.com")
                    .setPassword("userPassword");

            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonHelper.objectToJson(signUpRequest)))
                    .andExpect(status().isCreated());
        }

        @Test
        public void when_signUp_throw_NotFoundException_should_send_not_found_error() throws Exception {
            var signUpRequest = new SignUpRequest()
                    .setUsername("user name")
                    .setEmail("user@gmail.com")
                    .setPassword("userPassword");

            when(mockSignUp.execute(
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    signUpRequest.getPassword(),
                    null
            )).thenThrow(new NotFoundException("not found"));

            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonHelper.objectToJson(signUpRequest)))
                    .andExpect(status().isNotFound());
        }
    }
}
