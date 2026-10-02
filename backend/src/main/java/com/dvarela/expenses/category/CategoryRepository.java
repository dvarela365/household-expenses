package com.dvarela.expenses.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>{
    List<Category> findAllByOrderByNameAsc();

    Optional<Category> findByName(String name);

    List<Category> findAllByArchivedFalseOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query("""
        select case when count(e) > 0 then true else false end
        from Expense e where e.category.id = :id
        """)
    boolean isInUse(@Param("id") Long id);

}
