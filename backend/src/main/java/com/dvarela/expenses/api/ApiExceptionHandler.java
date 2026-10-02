package com.dvarela.expenses.api;

import java.util.LinkedHashMap;
import java.util.Map;

import com.dvarela.expenses.category.CategoryInUseException;
import com.dvarela.expenses.category.CategoryNameTakenException;
import com.dvarela.expenses.expense.ExpenseConflictException;
import com.dvarela.expenses.expense.ExpenseNotFoundException;
import com.dvarela.expenses.expense.UnknownCategoryException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dvarela.expenses.category.CategoryNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "The request has invalid fields.");
        problem.setTitle("Validation failed");

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    ProblemDetail handleExpenseNotFound(ExpenseNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Expense not found");
        return problem;
    }

    @ExceptionHandler({ExpenseConflictException.class, OptimisticLockingFailureException.class})
    ProblemDetail handleConflict(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "The expense was modified by someone else. Reload it and try again.");
        problem.setTitle("Expense modified concurrently");
        return problem;
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    ProblemDetail handleCategoryNotFound(CategoryNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Category not found");
        return problem;
    }

    @ExceptionHandler(UnknownCategoryException.class)
    ProblemDetail handleUnknownCategory(UnknownCategoryException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), ex.getMessage());
        problem.setTitle("Category not found");
        return problem;
    }

    @ExceptionHandler(CategoryNameTakenException.class)
    ProblemDetail handleCategoryNameTaken(CategoryNameTakenException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Category name already in use");
        return problem;
    }

    @ExceptionHandler(CategoryInUseException.class)
    ProblemDetail handleCategoryInUse(CategoryInUseException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Category in use");
        return problem;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "The operation conflicts with existing data.");
        problem.setTitle("Data conflict");
        return problem;
    }
}