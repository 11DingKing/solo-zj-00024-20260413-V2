package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.requests.BudgetRequest;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserServiceLogicException;
import com.fullStack.expenseTracker.services.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/mywallet/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> createBudget(@RequestBody BudgetRequest budgetRequest)
            throws UserNotFoundException, UserServiceLogicException {
        return budgetService.createBudget(budgetRequest);
    }

    @GetMapping("/get")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getBudgetByMonth(@Param("userId") long userId,
                                                              @Param("month") int month,
                                                              @Param("year") long year)
            throws UserServiceLogicException {
        return budgetService.getBudgetByMonth(userId, month, year);
    }

    @PostMapping("/category/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> createCategoryBudget(@RequestBody BudgetRequest budgetRequest)
            throws UserNotFoundException, UserServiceLogicException {
        return budgetService.createCategoryBudget(budgetRequest);
    }

    @GetMapping("/category/get")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getCategoryBudget(
            @Param("userId") long userId,
            @Param("categoryId") Integer categoryId,
            @Param("month") int month,
            @Param("year") long year)
            throws UserServiceLogicException {
        return budgetService.getCategoryBudget(userId, categoryId, month, year);
    }

    @GetMapping("/category/all")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getAllCategoryBudgets(
            @Param("userId") long userId,
            @Param("month") int month,
            @Param("year") long year)
            throws UserServiceLogicException {
        return budgetService.getAllCategoryBudgets(userId, month, year);
    }

    @GetMapping("/progress")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getBudgetProgress(
            @Param("userId") long userId,
            @Param("month") int month,
            @Param("year") long year)
            throws UserServiceLogicException {
        return budgetService.getBudgetProgress(userId, month, year);
    }

    @DeleteMapping("/category/delete")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> deleteCategoryBudget(
            @Param("userId") long userId,
            @Param("categoryId") Integer categoryId,
            @Param("month") int month,
            @Param("year") long year)
            throws UserServiceLogicException {
        return budgetService.deleteCategoryBudget(userId, categoryId, month, year);
    }
}
