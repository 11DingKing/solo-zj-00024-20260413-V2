package com.fullStack.expenseTracker.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {
    long userId;
    Integer categoryId;
    double amount;
    Integer month;
    Long year;
}
