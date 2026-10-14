package com.dvarela.expenses.expense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends Repository<Expense, Long> {

    <S extends Expense> S save(S expense);

    Optional<Expense> findByIdAndHouseholdId(Long id, Long householdId);

    void delete(Expense expense);

    void flush();

    @Query("""
            select e from Expense e
            join fetch e.category
            where e.householdId = :householdId
              and e.date >= :from and e.date < :to
            order by e.date desc, e.id desc
            """)
    List<Expense> findAllInPeriodWithCategory(@Param("householdId") Long householdId,
                                              @Param("from") LocalDate from,
                                              @Param("to") LocalDate to);
}