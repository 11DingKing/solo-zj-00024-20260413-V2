package com.fullStack.expenseTracker.repository;

import com.fullStack.expenseTracker.models.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Budget findByUserIdAndMonthAndYear(long userId, int month, long year);
    
    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(long userId, Integer categoryId, int month, long year);
    
    List<Budget> findByUserIdAndMonthAndYearAndCategoryIdIsNotNull(long userId, int month, long year);
    
    void deleteByUserIdAndCategoryIdAndMonthAndYear(long userId, Integer categoryId, int month, long year);
}
