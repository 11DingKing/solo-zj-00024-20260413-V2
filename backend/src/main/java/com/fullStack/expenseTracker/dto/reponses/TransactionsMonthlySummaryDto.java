package com.fullStack.expenseTracker.dto.reponses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsMonthlySummaryDto {

    private int month;

    private double total_expense;

    private double total_income;

    private long totalExpenseInFen;

    private long totalIncomeInFen;

    private BigDecimal totalExpenseInYuan;

    private BigDecimal totalIncomeInYuan;
}
