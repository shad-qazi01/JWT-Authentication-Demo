package com.smartbiz.auth;

import com.smartbiz.auth.dto.LoginRequest;
import com.smartbiz.auth.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc/*(addFilters = false)*/
public class ControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testLoginAPI_validatesResponseBody() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Register user via API (exercise register endpoint too)
        var regReq = new RegisterRequest("test@example.com", "secret12");
        String regJson = mapper.writeValueAsString(regReq);

        mockMvc.perform(post("/apis/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(regJson))
                .andExpect(status().isCreated());

        // Now login
        LoginRequest loginReq = new LoginRequest("test@example.com", "secret12");
        String loginJson = mapper.writeValueAsString(loginReq);

        mockMvc.perform(post("/apis/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresInSeconds").isNumber());
    }

    @Test
    public void testLoginAPI_invalidCredentials_returns4xx() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Register user
        var regReq = new RegisterRequest("baduser@example.com", "theRightPass");
        String regJson = mapper.writeValueAsString(regReq);
        mockMvc.perform(post("/apis/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(regJson))
                .andExpect(status().isCreated());

        // Attempt login with wrong password
        LoginRequest loginReq = new LoginRequest("baduser@example.com", "wrong pass");
        String loginJson = mapper.writeValueAsString(loginReq);

        mockMvc.perform(post("/apis/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testLoginAPI_validationErrors_returnsBadRequest() throws Exception {
        // Missing password
        String invalidJson = "{\"email\": \"no-password@example.com\"}";

        mockMvc.perform(post("/apis/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

}
