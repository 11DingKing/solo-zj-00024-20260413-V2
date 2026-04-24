package com.fullStack.expenseTracker.dto.reponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySummaryDto {

    private Integer categoryId;

    private String categoryName;

    private long totalAmount;

    private BigDecimal totalAmountInYuan;

    private BigDecimal percentage;

    private int transactionCount;
}
