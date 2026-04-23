package com.fullStack.expenseTracker.services.impls;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.reponses.BudgetProgressDto;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import com.fullStack.expenseTracker.dto.requests.BudgetRequest;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserServiceLogicException;
import com.fullStack.expenseTracker.models.Budget;
import com.fullStack.expenseTracker.models.Category;
import com.fullStack.expenseTracker.repository.BudgetRepository;
import com.fullStack.expenseTracker.repository.CategoryRepository;
import com.fullStack.expenseTracker.repository.TransactionRepository;
import com.fullStack.expenseTracker.repository.UserRepository;
import com.fullStack.expenseTracker.services.BudgetService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> createBudget(BudgetRequest budgetRequest) throws UserNotFoundException, UserServiceLogicException {
        if (!userRepository.existsById(budgetRequest.getUserId())) {
            throw new UserNotFoundException("User not found with id " + budgetRequest.getUserId());
        }
        try {
            Budget budget = budgetRepository.findByUserIdAndMonthAndYear(budgetRequest.getUserId(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
            if (budget == null){
                budget = new Budget(
                        budgetRequest.getUserId(), null, budgetRequest.getAmount(), LocalDate.now().getMonthValue(), LocalDate.now().getYear()
                );
            }
            else {
                budget.setAmount(budgetRequest.getAmount());
            }

            budgetRepository.save(budget);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(
                    ApiResponseStatus.SUCCESS,
                    HttpStatus.CREATED,
                    "Budget created successfully!"
            ));
        }catch (Exception e) {
            log.error("Failed to create budget: " + e.getMessage());
            throw new UserServiceLogicException("Failed to create budget: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getBudgetByMonth(long userId, int month, long year) throws UserServiceLogicException {
        try {
            Budget budget = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);
            double amount = budget == null ? 0 : budget.getAmount();

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                    ApiResponseStatus.SUCCESS,
                    HttpStatus.OK,
                    amount
            ));
        }catch (Exception e) {
            log.error("Failed to fetch budget amount: " + e.getMessage());
            throw new UserServiceLogicException("Failed to fetch budget: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> createCategoryBudget(BudgetRequest budgetRequest) throws UserNotFoundException, UserServiceLogicException {
        if (!userRepository.existsById(budgetRequest.getUserId())) {
            throw new UserNotFoundException("User not found with id " + budgetRequest.getUserId());
        }
        if (budgetRequest.getCategoryId() == null) {
            throw new UserServiceLogicException("CategoryId is required for category budget!");
        }
        try {
            int month = budgetRequest.getMonth() != null ? budgetRequest.getMonth() : LocalDate.now().getMonthValue();
            long year = budgetRequest.getYear() != null ? budgetRequest.getYear() : LocalDate.now().getYear();
            
            Optional<Budget> existingBudget = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                    budgetRequest.getUserId(), budgetRequest.getCategoryId(), month, year);
            
            Budget budget;
            if (existingBudget.isEmpty()) {
                budget = new Budget(
                        budgetRequest.getUserId(), 
                        budgetRequest.getCategoryId(), 
                        budgetRequest.getAmount(), 
                        month, 
                        year
                );
            } else {
                budget = existingBudget.get();
                budget.setAmount(budgetRequest.getAmount());
            }

            budgetRepository.save(budget);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(
                    ApiResponseStatus.SUCCESS,
                    HttpStatus.CREATED,
                    "Category budget created successfully!"
            ));
        } catch (Exception e) {
            log.error("Failed to create category budget: " + e.getMessage());
            throw new UserServiceLogicException("Failed to create category budget: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getCategoryBudget(long userId, Integer categoryId, int month, long year) throws UserServiceLogicException {
        try {
            Optional<Budget> budget = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(userId, categoryId, month, year);
            
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                    ApiResponseStatus.SUCCESS,
                    HttpStatus.OK,
                    budget.map(Budget::getAmount).orElse(0.0)
            ));
        } catch (Exception e) {
            log.error("Failed to fetch category budget: " + e.getMessage());
            throw new UserServiceLogicException("Failed to fetch category budget: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllCategoryBudgets(long userId, int month, long year) throws UserServiceLogicException {
        try {
            List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYearAndCategoryIdIsNotNull(userId, month, year);
            
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                    ApiResponseStatus.SUCCESS,
                    HttpStatus.OK,
                    budgets
            ));
        } catch (Exception e) {
            log.error("Failed to fetch category budgets: " + e.getMessage());
            throw new UserServiceLogicException("Failed to fetch category budgets: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getBudgetProgress(long userId, int month, long year) throws UserServiceLogicException {
        try {
            List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYearAndCategoryIdIsNotNull(userId, month, year);
            List<BudgetProgressDto> progressList = new ArrayList<>();
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new UserServiceLogicException("User not found!");
            }
            String userEmail = userOpt.get().getEmail();
            
            for (Budget budget : budgets) {
                if (budget.getCategoryId() == null) {
                    continue;
                }
                
                Optional<Category> categoryOpt = categoryRepository.findById(budget.getCategoryId());
                if (categoryOpt.isEmpty()) {
                    continue;
                }
                Category category = categoryOpt.get();
                
                Double spentAmount = transactionRepository.findTotalByUserAndCategory(
                        userEmail, budget.getCategoryId(), month, (int) year);
                spentAmount = spentAmount == null ? 0.0 : spentAmount;
                
                double percentage = budget.getAmount() > 0 ? (spentAmount / budget.getAmount()) * 100 : 0;
                
                BudgetProgressDto progress = new BudgetProgressDto(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        budget.getAmount(),
                        spentAmount,
                        percentage
                );
                progressList.add(progress);
            }
            
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                    ApiResponseStatus.SUCCESS,
                    HttpStatus.OK,
                    progressList
            ));
        } catch (Exception e) {
            log.error("Failed to fetch budget progress: " + e.getMessage());
            throw new UserServiceLogicException("Failed to fetch budget progress: Try again later!");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseDto<?>> deleteCategoryBudget(long userId, Integer categoryId, int month, long year) throws UserServiceLogicException {
        try {
            budgetRepository.deleteByUserIdAndCategoryIdAndMonthAndYear(userId, categoryId, month, year);
            
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                    ApiResponseStatus.SUCCESS,
                    HttpStatus.OK,
                    "Category budget deleted successfully!"
            ));
        } catch (Exception e) {
            log.error("Failed to delete category budget: " + e.getMessage());
            throw new UserServiceLogicException("Failed to delete category budget: Try again later!");
        }
    }
}
