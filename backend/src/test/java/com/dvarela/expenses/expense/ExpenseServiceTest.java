package com.dvarela.expenses.expense;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dvarela.expenses.category.Category;
import com.dvarela.expenses.category.CategoryKind;
import com.dvarela.expenses.category.CategoryNotFoundException;
import com.dvarela.expenses.category.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private final Category supermercado =
            new Category("Supermercado", CategoryKind.VARIABLE, "shopping-cart");

    @Test
    void register_savesExpenseWithItsCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(supermercado));
        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateExpenseRequest request = new CreateExpenseRequest(
                new BigDecimal("15300.50"), LocalDate.of(2026, 10, 1), 1L,
                PaymentMethod.DEBIT, "Coto", null);

        ExpenseResponse response = expenseService.register(request);

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(captor.capture());
        Expense saved = captor.getValue();

        assertThat(saved.getCategory()).isSameAs(supermercado);
        assertThat(saved.getAmount()).isEqualByComparingTo("15300.50");
        assertThat(saved.getMerchant()).isEqualTo("Coto");
        assertThat(response.categoryName()).isEqualTo("Supermercado");
    }

    @Test
    void register_failsWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        CreateExpenseRequest request = new CreateExpenseRequest(
                new BigDecimal("100"), LocalDate.of(2026, 10, 1), 99L,
                PaymentMethod.CASH, null, null);

        assertThatThrownBy(() -> expenseService.register(request))
                .isInstanceOf(CategoryNotFoundException.class);
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void findByMonth_queriesTheWholeMonthAndSumsAmounts() {
        when(expenseRepository.findAllInPeriodWithCategory(
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 1)))
                .thenReturn(List.of(
                        expense("100.25", 5),
                        expense("200.25", 20)));

        MonthlyExpensesResponse result = expenseService.findByMonth(YearMonth.of(2026, 10));

        assertThat(result.total()).isEqualByComparingTo("300.50");
        assertThat(result.expenses()).hasSize(2);
    }

    @Test
    void findByMonth_returnsZeroTotalForEmptyMonth() {
        when(expenseRepository.findAllInPeriodWithCategory(any(), any())).thenReturn(List.of());

        MonthlyExpensesResponse result = expenseService.findByMonth(YearMonth.of(2026, 10));

        assertThat(result.total()).isEqualByComparingTo("0");
        assertThat(result.expenses()).isEmpty();
    }

    private Expense expense(String amount, int day) {
        return new Expense(new BigDecimal(amount), LocalDate.of(2026, 10, day),
                supermercado, PaymentMethod.DEBIT, null, null);
    }
}