package com.fullStack.expenseTracker.dto.reponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRangeStatisticsDto {

    private long totalIncome;

    private BigDecimal totalIncomeInYuan;

    private long totalExpense;

    private BigDecimal totalExpenseInYuan;

    private BigDecimal netAmountInYuan;

    private int totalTransactions;

    private List<CategorySummaryDto> incomeByCategory;

    private List<CategorySummaryDto> expenseByCategory;

    private String startDate;

    private String endDate;
}
