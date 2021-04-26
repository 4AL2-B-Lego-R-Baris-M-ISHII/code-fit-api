package fr.esgi.pa.server.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    public void testAll() throws Exception {
        mockMvc.perform(get("/api/test/all")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testUser() throws Exception {
        mockMvc.perform(get("/api/test/user")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAdmin() throws Exception {
        mockMvc.perform(get("/api/test/admin")).andExpect(status().isOk());
    }
}