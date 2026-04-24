package com.fullStack.expenseTracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoryId")
    private Category category;
    private String description;
    private long amount;
    private LocalDate date;

    public Transaction(User user, Category category, String description, long amount, LocalDate date) {
        this.user = user;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public static long yuanToFen(BigDecimal yuan) {
        if (yuan == null) {
            return 0;
        }
        return yuan.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).longValue();
    }

    public static BigDecimal fenToYuan(long fen) {
        return new BigDecimal(fen).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public static long yuanToFen(double yuan) {
        return yuanToFen(BigDecimal.valueOf(yuan));
    }

    public static double fenToYuanAsDouble(long fen) {
        return fenToYuan(fen).doubleValue();
    }


}
