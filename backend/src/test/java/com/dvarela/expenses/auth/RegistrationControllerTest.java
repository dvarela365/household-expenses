package com.dvarela.expenses.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private RegistrationService registrationService;

    @Test
    void register_returns201() {
        when(registrationService.register(any())).thenReturn(
                new RegistrationResponse(7L, "ana@example.com", "Ana"));

        assertThat(mvc.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "ana@example.com", "password": "secreto123", "displayName": "Ana"}
                        """))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson().extractingPath("$.id").isEqualTo(7);
    }

    @Test
    void register_returns400ForAShortPassword() {
        assertThat(mvc.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "ana@example.com", "password": "corta", "displayName": "Ana"}
                        """))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().extractingPath("$.errors.password").isNotNull();
    }

    @Test
    void register_returns409WhenTheEmailIsTaken() {
        when(registrationService.register(any())).thenThrow(new EmailAlreadyRegisteredException());

        assertThat(mvc.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "ana@example.com", "password": "secreto123", "displayName": "Ana"}
                        """))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson().extractingPath("$.title").isEqualTo("Email already registered");
    }
}