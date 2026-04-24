package com.fullStack.expenseTracker.services.impls;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.reponses.CategorySummaryDto;
import com.fullStack.expenseTracker.dto.reponses.DateRangeStatisticsDto;
import com.fullStack.expenseTracker.dto.reponses.TransactionsMonthlySummaryDto;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import com.fullStack.expenseTracker.models.Transaction;
import com.fullStack.expenseTracker.repository.TransactionRepository;
import com.fullStack.expenseTracker.services.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ReportServiceImpl implements ReportService {

    private static final int INCOME_TYPE_ID = 2;
    private static final int EXPENSE_TYPE_ID = 1;

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> getTotalByTransactionTypeAndUser(Long userId, int transactionTypeId, int month, int year) {
        Long totalInFen = transactionRepository.findTotalByUserAndTransactionType(userId, transactionTypeId, month, year);
        if (totalInFen == null) {
            totalInFen = 0L;
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        Transaction.fenToYuanAsDouble(totalInFen)
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getTotalNoOfTransactionsByUser(Long userId, int month, int year) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        transactionRepository.findTotalNoOfTransactionsByUser(userId, month, year)
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getTotalExpenseByCategoryAndUser(String email, int categoryId, int month, int year) {
        Long totalInFen = transactionRepository.findTotalByUserAndCategory(email, categoryId, month, year);
        if (totalInFen == null) {
            totalInFen = 0L;
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        Transaction.fenToYuanAsDouble(totalInFen)
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getMonthlySummaryByUser(String email) {
        List<Object[]> result = transactionRepository.findMonthlySummaryByUser(email);

        List<TransactionsMonthlySummaryDto> transactionsMonthlySummary = result.stream()
                .map(data -> {
                    int month = (int) data[0];
                    long expenseInFen = ((Number) data[1]).longValue();
                    long incomeInFen = ((Number) data[2]).longValue();
                    
                    return new TransactionsMonthlySummaryDto(
                            month,
                            Transaction.fenToYuanAsDouble(expenseInFen),
                            Transaction.fenToYuanAsDouble(incomeInFen),
                            expenseInFen,
                            incomeInFen,
                            Transaction.fenToYuan(expenseInFen),
                            Transaction.fenToYuan(incomeInFen)
                    );
                }).toList();

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        transactionsMonthlySummary
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getStatisticsByDateRange(String email, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponseDto<>(ApiResponseStatus.FAILED,
                            HttpStatus.BAD_REQUEST,
                            "Start date and end date are required!")
            );
        }

        if (startDate.isAfter(endDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponseDto<>(ApiResponseStatus.FAILED,
                            HttpStatus.BAD_REQUEST,
                            "Start date cannot be after end date!")
            );
        }

        Long totalIncomeInFen = transactionRepository.findTotalByEmailAndTransactionTypeAndDateRange(
                email, INCOME_TYPE_ID, startDate, endDate);
        Long totalExpenseInFen = transactionRepository.findTotalByEmailAndTransactionTypeAndDateRange(
                email, EXPENSE_TYPE_ID, startDate, endDate);
        Integer totalTransactions = transactionRepository.countByUserAndDateRange(
                email, startDate, endDate);

        if (totalIncomeInFen == null) totalIncomeInFen = 0L;
        if (totalExpenseInFen == null) totalExpenseInFen = 0L;
        if (totalTransactions == null) totalTransactions = 0;

        List<CategorySummaryDto> incomeByCategory = buildCategorySummary(
                email, INCOME_TYPE_ID, startDate, endDate, totalIncomeInFen);
        List<CategorySummaryDto> expenseByCategory = buildCategorySummary(
                email, EXPENSE_TYPE_ID, startDate, endDate, totalExpenseInFen);

        DateRangeStatisticsDto statistics = new DateRangeStatisticsDto();
        statistics.setTotalIncome(totalIncomeInFen);
        statistics.setTotalIncomeInYuan(Transaction.fenToYuan(totalIncomeInFen));
        statistics.setTotalExpense(totalExpenseInFen);
        statistics.setTotalExpenseInYuan(Transaction.fenToYuan(totalExpenseInFen));
        statistics.setNetAmountInYuan(Transaction.fenToYuan(totalIncomeInFen - totalExpenseInFen));
        statistics.setTotalTransactions(totalTransactions);
        statistics.setIncomeByCategory(incomeByCategory);
        statistics.setExpenseByCategory(expenseByCategory);
        statistics.setStartDate(startDate.toString());
        statistics.setEndDate(endDate.toString());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        statistics)
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getCategorySummaryByDateRange(String email, int transactionTypeId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponseDto<>(ApiResponseStatus.FAILED,
                            HttpStatus.BAD_REQUEST,
                            "Start date and end date are required!")
            );
        }

        Long totalAmountInFen = transactionRepository.findTotalByEmailAndTransactionTypeAndDateRange(
                email, transactionTypeId, startDate, endDate);
        if (totalAmountInFen == null) totalAmountInFen = 0L;

        List<CategorySummaryDto> categorySummary = buildCategorySummary(
                email, transactionTypeId, startDate, endDate, totalAmountInFen);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        categorySummary)
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getCategorySummaryByMonth(String email, int transactionTypeId, int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        Long totalAmountInFen = transactionRepository.findTotalByEmailAndTransactionTypeAndDateRange(
                email, transactionTypeId, startDate, endDate);
        if (totalAmountInFen == null) totalAmountInFen = 0L;

        List<Object[]> rawResult = transactionRepository.findSummaryByCategoryAndMonth(
                email, transactionTypeId, month, year);

        List<CategorySummaryDto> categorySummary = buildCategorySummaryFromRaw(rawResult, totalAmountInFen);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        categorySummary)
        );
    }

    private List<CategorySummaryDto> buildCategorySummary(
            String email, int transactionTypeId, LocalDate startDate, LocalDate endDate, long totalAmountInFen) {
        
        List<Object[]> rawResult = transactionRepository.findSummaryByCategoryAndDateRange(
                email, transactionTypeId, startDate, endDate);
        
        return buildCategorySummaryFromRaw(rawResult, totalAmountInFen);
    }

    private List<CategorySummaryDto> buildCategorySummaryFromRaw(List<Object[]> rawResult, long totalAmountInFen) {
        List<CategorySummaryDto> result = new ArrayList<>();
        
        for (Object[] row : rawResult) {
            Integer categoryId = ((Number) row[0]).intValue();
            String categoryName = (String) row[1];
            long amountInFen = ((Number) row[2]).longValue();
            int transactionCount = ((Number) row[3]).intValue();

            BigDecimal percentage = BigDecimal.ZERO;
            if (totalAmountInFen > 0) {
                percentage = BigDecimal.valueOf(amountInFen)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalAmountInFen), 2, RoundingMode.HALF_UP);
            }

            CategorySummaryDto dto = new CategorySummaryDto();
            dto.setCategoryId(categoryId);
            dto.setCategoryName(categoryName);
            dto.setTotalAmount(amountInFen);
            dto.setTotalAmountInYuan(Transaction.fenToYuan(amountInFen));
            dto.setPercentage(percentage);
            dto.setTransactionCount(transactionCount);
            
            result.add(dto);
        }
        
        return result;
    }
}
