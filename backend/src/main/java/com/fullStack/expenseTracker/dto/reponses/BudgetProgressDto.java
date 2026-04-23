package com.fullStack.expenseTracker.dto.reponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProgressDto {
    private Integer categoryId;
    private String categoryName;
    private double budgetAmount;
    private double spentAmount;
    private double percentage;
}
