package com.ademarporto.ls.service;

import com.ademarporto.ls.model.Client;
import com.ademarporto.ls.model.Loan;

import java.math.BigDecimal;
import java.util.UUID;

public interface LoanService {
    BigDecimal calculateEmi(BigDecimal loanValue, BigDecimal interestRate, int loanTerm);

    Client addLoanToClient(UUID clientId, Loan loan);

    void removeLoanFromClient(UUID clientId, UUID loanId);

    Client updateClientLoan(UUID clientId, Loan loan);

    Client findClientById(UUID clientId);

}
