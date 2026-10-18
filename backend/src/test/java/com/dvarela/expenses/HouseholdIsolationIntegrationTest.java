package com.dvarela.expenses;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class HouseholdIsolationIntegrationTest {

    private static final String PASSWORD = "secreto123";

    @Autowired
    private MockMvcTester mvc;

    @Test
    void aUserCannotSeeOrTouchTheExpensesOfAnotherHousehold() throws Exception {
        String anaEmail = "ana-" + UUID.randomUUID() + "@example.com";
        String brunoEmail = "bruno-" + UUID.randomUUID() + "@example.com";
        register(anaEmail, "Ana");
        register(brunoEmail, "Bruno");
        MockHttpSession ana = login(anaEmail);
        MockHttpSession bruno = login(brunoEmail);

        // Ana creates an expense in one of her own categories.
        String anaCategories = mvc.get().uri("/api/categories").session(ana)
                .exchange().getResponse().getContentAsString();
        Integer anaSupermercado = JsonPath.<List<Integer>>read(
                anaCategories, "$[?(@.name == 'Supermercado')].id").get(0);

        String created = mvc.post().uri("/api/expenses").session(ana).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"amount": 1500, "date": "2026-10-10", "categoryId": %d, "paymentMethod": "CASH"}
                        """.formatted(anaSupermercado))
                .exchange().getResponse().getContentAsString();
        Integer anaExpenseId = JsonPath.read(created, "$.id");

        // Bruno does not see it in his month...
        assertThat(mvc.get().uri("/api/expenses?month=2026-10").session(bruno))
                .hasStatusOk()
                .bodyJson().extractingPath("$.expenses.length()").isEqualTo(0);

        // ...cannot edit it...
        assertThat(mvc.put().uri("/api/expenses/" + anaExpenseId).session(bruno).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"amount": 1, "date": "2026-10-10", "categoryId": %d,
                         "paymentMethod": "CASH", "version": 0}
                        """.formatted(anaSupermercado)))
                .hasStatus(HttpStatus.NOT_FOUND);

        // ...cannot delete it...
        assertThat(mvc.delete().uri("/api/expenses/" + anaExpenseId).session(bruno).with(csrf()))
                .hasStatus(HttpStatus.NOT_FOUND);

        // ...and cannot attach his own expense to Ana's category.
        assertThat(mvc.post().uri("/api/expenses").session(bruno).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"amount": 100, "date": "2026-10-10", "categoryId": %d, "paymentMethod": "CASH"}
                        """.formatted(anaSupermercado)))
                .hasStatus(422);

        // Ana's expense is untouched.
        assertThat(mvc.get().uri("/api/expenses?month=2026-10").session(ana))
                .hasStatusOk()
                .bodyJson().extractingPath("$.expenses.length()").isEqualTo(1);
    }

    @Test
    void theApiRequiresASession() {
        assertThat(mvc.get().uri("/api/expenses?month=2026-10"))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }

    private void register(String email, String displayName) {
        assertThat(mvc.post().uri("/api/auth/register").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "%s", "password": "%s", "displayName": "%s"}
                        """.formatted(email, PASSWORD, displayName)))
                .hasStatus(HttpStatus.CREATED);
    }

    private MockHttpSession login(String email) {
        var result = mvc.post().uri("/api/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "%s", "password": "%s"}
                        """.formatted(email, PASSWORD))
                .exchange();
        assertThat(result).hasStatusOk();
        return (MockHttpSession) result.getMvcResult().getRequest().getSession(false);
    }
}