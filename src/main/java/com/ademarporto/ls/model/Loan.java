package com.ademarporto.ls.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record Loan(UUID loanId,
                   BigDecimal loanValue,
                   BigDecimal interestRate,
                   int loanTerm,
                   BigDecimal monthlyPayment,
                   LoanType loanType,
                   LocalDate createdDate) {
}
