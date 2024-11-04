package com.ademarporto.ls.testutils;

import com.ademarporto.ls.model.Loan;
import com.ademarporto.ls.model.LoanType;
import com.ademarporto.ls.repository.entity.LoanEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class LoanFactory {

    private LoanFactory() {
    }

    public static final BigDecimal LOAN_VALUE = new BigDecimal(300_000);
    public static final BigDecimal NEW_LOAN_VALUE = new BigDecimal(30000);
    public static final BigDecimal INTEREST_RATE = new BigDecimal(1);
    public static final int LOAN_TERM = 30;
    public static final BigDecimal EMI_RESULT = BigDecimal.valueOf(964.92);
    public static final BigDecimal EMI_RESULT_WITHOUT_INTEREST_RATE = BigDecimal.valueOf(833.34);


    public static LoanEntity createLoanEntity() {
        var loan = new LoanEntity();
        loan.setLoanType(LoanType.HOME);
        loan.setLoanTerm(LOAN_TERM);
        loan.setLoanValue(LOAN_VALUE);
        loan.setInterestRate(INTEREST_RATE);
        loan.setMonthlyPayment(EMI_RESULT);
        return loan;
    }

    public static Loan createLoanDto(UUID loanId, LoanType loanType) {
        return new Loan(loanId,
                        LOAN_VALUE,
                        INTEREST_RATE,
                        LOAN_TERM,
                null,
                        loanType,
                        LocalDate.parse("2024-11-04"));
    }

    public static com.ademarporto.ls.rest.spec.Loan createLoanSpec() {
        var loan = new com.ademarporto.ls.rest.spec.Loan();
        loan.setLoanValue(LOAN_VALUE);
        loan.setInterestRate(INTEREST_RATE);
        loan.setLoanTerm(LOAN_TERM);
        loan.setLoanType(com.ademarporto.ls.rest.spec.LoanType.HOME);
        return loan;
    }

    public static com.ademarporto.ls.rest.spec.Loan createLoanSpec(UUID loanId,
                                                                   com.ademarporto.ls.rest.spec.LoanType loanType) {
        var loan = new com.ademarporto.ls.rest.spec.Loan();
        loan.setLoanId(loanId);
        loan.setLoanValue(LOAN_VALUE);
        loan.setInterestRate(INTEREST_RATE);
        loan.setLoanTerm(LOAN_TERM);
        loan.setLoanType(loanType);
        return loan;
    }

}
