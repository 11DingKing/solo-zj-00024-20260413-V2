package com.fullStack.expenseTracker.services;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface ReportService {
    ResponseEntity<ApiResponseDto<?>> getTotalByTransactionTypeAndUser(Long userId, int transactionTypeId, int month, int year);

    ResponseEntity<ApiResponseDto<?>> getTotalNoOfTransactionsByUser(Long userId, int month, int year);

    ResponseEntity<ApiResponseDto<?>> getTotalExpenseByCategoryAndUser(String email, int categoryId, int month, int year);

    ResponseEntity<ApiResponseDto<?>> getMonthlySummaryByUser(String email);

    ResponseEntity<ApiResponseDto<?>> getStatisticsByDateRange(String email, LocalDate startDate, LocalDate endDate);

    ResponseEntity<ApiResponseDto<?>> getCategorySummaryByDateRange(String email, int transactionTypeId, LocalDate startDate, LocalDate endDate);

    ResponseEntity<ApiResponseDto<?>> getCategorySummaryByMonth(String email, int transactionTypeId, int month, int year);

}
