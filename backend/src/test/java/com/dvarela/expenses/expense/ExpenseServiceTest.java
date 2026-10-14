package com.dvarela.expenses.expense;

import static com.dvarela.expenses.expense.ExpenseBuilder.anExpense;
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
import com.dvarela.expenses.category.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    private static final Long HOUSEHOLD = 1L;
    private static final Long OTHER_HOUSEHOLD = 2L;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private final Category supermercado =
            new Category(HOUSEHOLD, "Supermercado", CategoryKind.VARIABLE, "shopping-cart");

    @Test
    void register_savesExpenseWithItsCategoryAndHousehold() {
        when(categoryRepository.findByIdAndHouseholdId(1L, HOUSEHOLD)).thenReturn(Optional.of(supermercado));
        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateExpenseRequest request = new CreateExpenseRequest(
                new BigDecimal("15300.50"), LocalDate.of(2026, 10, 1), 1L,
                PaymentMethod.DEBIT, "Coto", null);

        ExpenseResponse response = expenseService.register(HOUSEHOLD, request);

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(captor.capture());
        Expense saved = captor.getValue();

        assertThat(saved.getHouseholdId()).isEqualTo(HOUSEHOLD);
        assertThat(saved.getCategory()).isSameAs(supermercado);
        assertThat(saved.getAmount()).isEqualByComparingTo("15300.50");
        assertThat(saved.getMerchant()).isEqualTo("Coto");
        assertThat(response.categoryName()).isEqualTo("Supermercado");
    }

    @Test
    void register_failsWhenCategoryDoesNotExist() {
        when(categoryRepository.findByIdAndHouseholdId(99L, HOUSEHOLD)).thenReturn(Optional.empty());

        CreateExpenseRequest request = new CreateExpenseRequest(
                new BigDecimal("100"), LocalDate.of(2026, 10, 1), 99L,
                PaymentMethod.CASH, null, null);

        assertThatThrownBy(() -> expenseService.register(HOUSEHOLD, request))
                .isInstanceOf(UnknownCategoryException.class);
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void register_rejectsACategoryOfAnotherHousehold() {
        when(categoryRepository.findByIdAndHouseholdId(1L, OTHER_HOUSEHOLD)).thenReturn(Optional.empty());

        CreateExpenseRequest request = new CreateExpenseRequest(
                new BigDecimal("100"), LocalDate.of(2026, 10, 1), 1L,
                PaymentMethod.CASH, null, null);

        assertThatThrownBy(() -> expenseService.register(OTHER_HOUSEHOLD, request))
                .isInstanceOf(UnknownCategoryException.class);
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void findByMonth_queriesTheWholeMonthAndSumsAmounts() {
        when(expenseRepository.findAllInPeriodWithCategory(
                HOUSEHOLD, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 1)))
                .thenReturn(List.of(
                        anExpense().withAmount("100.25").onDate(LocalDate.of(2026, 10, 5)).inCategory(supermercado).build(),
                        anExpense().withAmount("200.25").onDate(LocalDate.of(2026, 10, 20)).inCategory(supermercado).build()));

        MonthlyExpensesResponse result = expenseService.findByMonth(HOUSEHOLD, YearMonth.of(2026, 10));

        assertThat(result.total()).isEqualByComparingTo("300.50");
        assertThat(result.expenses()).hasSize(2);
    }

    @Test
    void findByMonth_returnsZeroTotalForEmptyMonth() {
        when(expenseRepository.findAllInPeriodWithCategory(any(), any(), any())).thenReturn(List.of());

        MonthlyExpensesResponse result = expenseService.findByMonth(HOUSEHOLD, YearMonth.of(2026, 10));

        assertThat(result.total()).isEqualByComparingTo("0");
        assertThat(result.expenses()).isEmpty();
    }

    @Test
    void update_changesTheExpenseAndReturnsTheNewData() {
        Expense expense = anExpense().inCategory(supermercado).persisted(10L, 3L).build();
        Category delivery = new Category(HOUSEHOLD, "Delivery", CategoryKind.VARIABLE, "bike");
        when(expenseRepository.findByIdAndHouseholdId(10L, HOUSEHOLD)).thenReturn(Optional.of(expense));
        when(categoryRepository.findByIdAndHouseholdId(2L, HOUSEHOLD)).thenReturn(Optional.of(delivery));

        UpdateExpenseRequest request = new UpdateExpenseRequest(
                new BigDecimal("999.99"), LocalDate.of(2026, 10, 3), 2L,
                PaymentMethod.CASH, "Rappi", null, 3L);

        ExpenseResponse response = expenseService.update(HOUSEHOLD, 10L, request);

        assertThat(expense.getAmount()).isEqualByComparingTo("999.99");
        assertThat(expense.getCategory()).isSameAs(delivery);
        assertThat(response.categoryName()).isEqualTo("Delivery");
        verify(expenseRepository).flush();
    }

    @Test
    void update_failsWhenTheVersionIsStale() {
        when(expenseRepository.findByIdAndHouseholdId(10L, HOUSEHOLD)).thenReturn(Optional.of(
                anExpense().inCategory(supermercado).persisted(10L, 4L).build()));

        UpdateExpenseRequest staleRequest = new UpdateExpenseRequest(
                new BigDecimal("100"), LocalDate.of(2026, 10, 1), 1L,
                PaymentMethod.CASH, null, null, 3L);

        assertThatThrownBy(() -> expenseService.update(HOUSEHOLD, 10L, staleRequest))
                .isInstanceOf(ExpenseConflictException.class);
        verify(expenseRepository, never()).flush();
    }

    @Test
    void update_failsWhenTheExpenseDoesNotExist() {
        when(expenseRepository.findByIdAndHouseholdId(99L, HOUSEHOLD)).thenReturn(Optional.empty());

        UpdateExpenseRequest request = new UpdateExpenseRequest(
                new BigDecimal("100"), LocalDate.of(2026, 10, 1), 1L,
                PaymentMethod.CASH, null, null, 0L);

        assertThatThrownBy(() -> expenseService.update(HOUSEHOLD, 99L, request))
                .isInstanceOf(ExpenseNotFoundException.class);
    }

    @Test
    void update_failsForAnExpenseOfAnotherHousehold() {
        when(expenseRepository.findByIdAndHouseholdId(10L, OTHER_HOUSEHOLD)).thenReturn(Optional.empty());

        UpdateExpenseRequest request = new UpdateExpenseRequest(
                new BigDecimal("100"), LocalDate.of(2026, 10, 1), 1L,
                PaymentMethod.CASH, null, null, 0L);

        assertThatThrownBy(() -> expenseService.update(OTHER_HOUSEHOLD, 10L, request))
                .isInstanceOf(ExpenseNotFoundException.class);
        verify(expenseRepository, never()).flush();
    }

    @Test
    void delete_removesTheExpense() {
        Expense expense = anExpense().inCategory(supermercado).persisted(10L, 0L).build();
        when(expenseRepository.findByIdAndHouseholdId(10L, HOUSEHOLD)).thenReturn(Optional.of(expense));

        expenseService.delete(HOUSEHOLD, 10L);

        verify(expenseRepository).delete(expense);
    }

    @Test
    void delete_failsWhenTheExpenseDoesNotExist() {
        when(expenseRepository.findByIdAndHouseholdId(99L, HOUSEHOLD)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.delete(HOUSEHOLD, 99L))
                .isInstanceOf(ExpenseNotFoundException.class);
        verify(expenseRepository, never()).delete(any());
    }
}