package com.dvarela.expenses.expense;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("""
            select e from Expense e
            join fetch e.category
            where e.date >= :from and e.date < :to
            order by e.date desc, e.id desc
            """)
    List<Expense> findAllInPeriodWithCategory(@Param("from") LocalDate from,
                                              @Param("to") LocalDate to);
}