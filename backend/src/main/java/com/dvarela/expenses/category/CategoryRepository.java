package com.dvarela.expenses.category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByHouseholdIdOrderByNameAsc(Long householdId);

    List<Category> findAllByHouseholdIdAndArchivedFalseOrderByNameAsc(Long householdId);

    Optional<Category> findByIdAndHouseholdId(Long id, Long householdId);

    boolean existsByHouseholdIdAndNameIgnoreCase(Long householdId, String name);

    boolean existsByHouseholdIdAndNameIgnoreCaseAndIdNot(Long householdId, String name, Long id);

    Optional<Category> findByName(String name);

    @Query("""
        select case when count(e) > 0 then true else false end
        from Expense e where e.category.id = :id
        """)
    boolean isInUse(@Param("id") Long id);
}