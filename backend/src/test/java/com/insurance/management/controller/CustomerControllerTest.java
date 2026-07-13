package com.insurance.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.management.dto.CustomerRequestDto;
import com.insurance.management.entity.AccountStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Test
    public void shouldReturn401WhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Unauthorized: Missing or invalid token"));
    }

    @Test
    public void shouldReturn403WhenAgentTriesToDelete() throws Exception {
        mockMvc.perform(delete("/api/customers/1")
                .header("X-Auth-Token", "AGENT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Forbidden: Agent role cannot perform DELETE operations"));
    }

    @Test
    public void shouldReturn400WhenInvalidEmailPassed() throws Exception {
        CustomerRequestDto invalidRequest = new CustomerRequestDto(
                "John",
                "Doe",
                "invalid-email-format",
                "1234567890",
                LocalDate.of(1990, 5, 15),
                AccountStatus.ACTIVE
        );

        mockMvc.perform(post("/api/customers")
                .header("X-Auth-Token", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").value("Invalid email format"));
    }

    @Test
    public void shouldReturn200WhenAdminFetchesAllCustomers() throws Exception {
        mockMvc.perform(get("/api/customers")
                .header("X-Auth-Token", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
