package com.fullStack.expenseTracker.services;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.requests.BudgetRequest;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserServiceLogicException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface BudgetService {
    ResponseEntity<ApiResponseDto<?>> createBudget(BudgetRequest budgetRequest) throws UserNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> getBudgetByMonth(long userId, int month, long year) throws UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> createCategoryBudget(BudgetRequest budgetRequest) throws UserNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> getCategoryBudget(long userId, Integer categoryId, int month, long year) throws UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> getAllCategoryBudgets(long userId, int month, long year) throws UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> getBudgetProgress(long userId, int month, long year) throws UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> deleteCategoryBudget(long userId, Integer categoryId, int month, long year) throws UserServiceLogicException;
}
