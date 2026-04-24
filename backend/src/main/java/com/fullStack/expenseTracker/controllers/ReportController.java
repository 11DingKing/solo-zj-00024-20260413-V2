package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/mywallet/report")
public class ReportController {

    @Autowired
    ReportService reportService;


    @GetMapping("/getTotalIncomeOrExpense")
    @PreAuthorize(("hasRole('ROLE_USER')"))
    public ResponseEntity<ApiResponseDto<?>> getTotalIncomeOrExpense(@Param("userId") Long userId,
                                                                     @Param("transactionTypeId") int transactionTypeId,
                                                                     @Param("month") int month,
                                                                     @Param("year") int year) {
        return reportService.getTotalByTransactionTypeAndUser(userId, transactionTypeId, month, year);
    }

    @GetMapping("/getTotalNoOfTransactions")
    @PreAuthorize(("hasRole('ROLE_USER')"))
    public ResponseEntity<ApiResponseDto<?>> getTotalNoOfTransactions(@Param("userId") Long userId,
                                                                      @Param("month") int month,
                                                                      @Param("year") int year) {
        return reportService.getTotalNoOfTransactionsByUser(userId, month, year);
    }

    @GetMapping("/getTotalByCategory")
    @PreAuthorize(("hasRole('ROLE_USER')"))
    public ResponseEntity<ApiResponseDto<?>> getTotalByCategory(@Param("email") String email,
                                                                @Param("categoryId") int categoryId,
                                                                @Param("month") int month,
                                                                @Param("year") int year) {
        return reportService.getTotalExpenseByCategoryAndUser(email, categoryId, month, year);
    }

    @GetMapping("/getMonthlySummaryByUser")
    @PreAuthorize(("hasRole('ROLE_USER')"))
    public ResponseEntity<ApiResponseDto<?>> getMonthlySummaryByUser(@Param("email") String email) {
        return reportService.getMonthlySummaryByUser(email);
    }

    @GetMapping("/getStatisticsByDateRange")
    @PreAuthorize(("hasRole('ROLE_USER')"))
    public ResponseEntity<ApiResponseDto<?>> getStatisticsByDateRange(
            @Param("email") String email,
            @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Param("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return reportService.getStatisticsByDateRange(email, startDate, endDate);
    }

    @GetMapping("/getCategorySummaryByDateRange")
    @PreAuthorize(("hasRole('ROLE_USER')"))
    public ResponseEntity<ApiResponseDto<?>> getCategorySummaryByDateRange(
            @Param("email") String email,
            @Param("transactionTypeId") int transactionTypeId,
            @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Param("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return reportService.getCategorySummaryByDateRange(email, transactionTypeId, startDate, endDate);
    }

    @GetMapping("/getCategorySummaryByMonth")
    @PreAuthorize(("hasRole('ROLE_USER')"))
    public ResponseEntity<ApiResponseDto<?>> getCategorySummaryByMonth(
            @Param("email") String email,
            @Param("transactionTypeId") int transactionTypeId,
            @Param("month") int month,
            @Param("year") int year) {
        return reportService.getCategorySummaryByMonth(email, transactionTypeId, month, year);
    }

}
