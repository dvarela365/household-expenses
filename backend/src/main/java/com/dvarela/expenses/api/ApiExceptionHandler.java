package com.dvarela.expenses.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dvarela.expenses.category.CategoryInUseException;
import com.dvarela.expenses.category.CategoryNameTakenException;
import com.dvarela.expenses.category.CategoryNotFoundException;
import com.dvarela.expenses.expense.ExpenseConflictException;
import com.dvarela.expenses.expense.ExpenseNotFoundException;
import com.dvarela.expenses.expense.UnknownCategoryException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = Problems.of(HttpStatus.BAD_REQUEST,
                "Validation failed", "The request has invalid fields.");
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    ProblemDetail handleCategoryNotFound(CategoryNotFoundException ex) {
        return Problems.of(HttpStatus.NOT_FOUND, "Category not found", ex.getMessage());
    }

    @ExceptionHandler(UnknownCategoryException.class)
    ProblemDetail handleUnknownCategory(UnknownCategoryException ex) {
        return Problems.of(Problems.UNPROCESSABLE_CONTENT, "Category not found", ex.getMessage());
    }

    @ExceptionHandler(CategoryNameTakenException.class)
    ProblemDetail handleCategoryNameTaken(CategoryNameTakenException ex) {
        return Problems.of(HttpStatus.CONFLICT, "Category name already in use", ex.getMessage());
    }

    @ExceptionHandler(CategoryInUseException.class)
    ProblemDetail handleCategoryInUse(CategoryInUseException ex) {
        return Problems.of(HttpStatus.CONFLICT, "Category in use", ex.getMessage());
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    ProblemDetail handleExpenseNotFound(ExpenseNotFoundException ex) {
        return Problems.of(HttpStatus.NOT_FOUND, "Expense not found", ex.getMessage());
    }

    @ExceptionHandler({ExpenseConflictException.class, OptimisticLockingFailureException.class})
    ProblemDetail handleExpenseConflict(RuntimeException ex) {
        return Problems.of(HttpStatus.CONFLICT, "Expense modified concurrently",
                "The expense was modified by someone else. Reload it and try again.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return Problems.of(HttpStatus.CONFLICT, "Data conflict",
                "The operation conflicts with existing data.");
    }
}