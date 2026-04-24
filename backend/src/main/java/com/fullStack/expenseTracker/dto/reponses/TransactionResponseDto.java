package com.fullStack.expenseTracker.dto.reponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDto {

    private Long transactionId;

    private int categoryId;

    private String categoryName;

    private int transactionType;

    private String description;

    private double amount;

    private long amountInFen;

    private BigDecimal amountInYuan;

    private LocalDate date;

    private String userEmail;

}
