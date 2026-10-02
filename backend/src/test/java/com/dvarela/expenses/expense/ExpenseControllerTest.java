package com.dvarela.expenses.expense;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.dvarela.expenses.category.CategoryNotFoundException;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ExpenseService expenseService;

    @Test
    void register_returns201WithTheCreatedExpense() {
        when(expenseService.register(any())).thenReturn(new ExpenseResponse(
                1L, new BigDecimal("15300.50"), LocalDate.of(2026, 10, 1),
                1L, "Supermercado", "shopping-cart", PaymentMethod.DEBIT, "Coto", null));

        assertThat(mvc.post().uri("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"amount": 15300.50, "date": "2026-10-01", "categoryId": 1,
                         "paymentMethod": "DEBIT", "merchant": "Coto"}
                        """))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson().extractingPath("$.categoryName").isEqualTo("Supermercado");
    }

    @Test
    void register_returns400WithFieldErrorsWhenAmountIsMissing() {
        assertThat(mvc.post().uri("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"date": "2026-10-01", "categoryId": 1, "paymentMethod": "DEBIT"}
                        """))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().extractingPath("$.errors.amount").isNotNull();

        verify(expenseService, never()).register(any());
    }

    @Test
    void register_returns422WhenCategoryDoesNotExist() {
        when(expenseService.register(any())).thenThrow(new CategoryNotFoundException(99L));

        assertThat(mvc.post().uri("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"amount": 100, "date": "2026-10-01", "categoryId": 99,
                         "paymentMethod": "CASH"}
                        """))
                .hasStatus(422)
                .bodyJson().extractingPath("$.title").isEqualTo("Category not found");
    }

    @Test
    void findByMonth_returnsTheMonthWithItsTotal() {
        when(expenseService.findByMonth(YearMonth.of(2026, 10))).thenReturn(
                new MonthlyExpensesResponse(YearMonth.of(2026, 10), new BigDecimal("300.50"), List.of()));

        assertThat(mvc.get().uri("/api/expenses?month=2026-10"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.month").isEqualTo("2026-10");
    }
}