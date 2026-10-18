package com.dvarela.expenses.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    private final AppUserPrincipal ana =
            new AppUserPrincipal(7L, 3L, "ana@example.com", "{bcrypt}hash", "Ana");

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    void login_returnsTheUserWithTheirHousehold() {
        when(authenticationManager.authenticate(any())).thenReturn(
                UsernamePasswordAuthenticationToken.authenticated(ana, null, ana.getAuthorities()));

        assertThat(mvc.post().uri("/api/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "ana@example.com", "password": "secreto123"}
                        """))
                .hasStatusOk()
                .bodyJson().extractingPath("$.householdId").isEqualTo(3);
    }

    @Test
    void login_returns401ForBadCredentials() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThat(mvc.post().uri("/api/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "ana@example.com", "password": "incorrecta"}
                        """))
                .hasStatus(HttpStatus.UNAUTHORIZED)
                .bodyJson().extractingPath("$.title").isEqualTo("Invalid credentials");
    }

    @Test
    void me_returnsTheLoggedInUser() {
        assertThat(mvc.get().uri("/api/auth/me").with(user(ana)))
                .hasStatusOk()
                .bodyJson().extractingPath("$.email").isEqualTo("ana@example.com");
    }
}