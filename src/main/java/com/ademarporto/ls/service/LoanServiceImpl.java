package com.ademarporto.ls.service;

import com.ademarporto.ls.exception.ClientNotFoundException;
import com.ademarporto.ls.exception.LoanNotFoundException;
import com.ademarporto.ls.mapper.ClientMapper;
import com.ademarporto.ls.mapper.LoanMapper;
import com.ademarporto.ls.model.Client;
import com.ademarporto.ls.model.Loan;
import com.ademarporto.ls.repository.ClientRepository;
import com.ademarporto.ls.repository.entity.ClientEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

import static com.ademarporto.ls.exception.ErrorMessage.CLIENT_NOT_FOUND_CODE;
import static com.ademarporto.ls.exception.ErrorMessage.CLIENT_NOT_FOUND_MESSAGE;
import static com.ademarporto.ls.exception.ErrorMessage.LOAN_NOT_FOUND_CODE;
import static com.ademarporto.ls.exception.ErrorMessage.LOAN_NOT_FOUND_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private static final int HUNDRED = 100;
    private static final int MONTHS = 12;

    private final ClientRepository clientRepository;
    private final LoanMapper loanMapper;
    private final ClientMapper clientMapper;

    /**
     * Method that calculates the EMI (Equated Monthly Installment), it uses the following formulae
     * to perform the calculation:
     * EMI = P x R x (1+R)^N / [(1+R)^N-1]
     * Where
     * “P” is the loan amount
     * “N” is tenure in months
     * “R” is the monthly interest
     *
     * @param loanValue    The loan amount.
     * @param interestRate The yearly interest rate.
     * @param loanTerm     The loan term.
     * @return the EMI Calculated.
     */
    @Override
    public BigDecimal calculateEmi(BigDecimal loanValue, BigDecimal interestRate, int loanTerm) {
        log.debug("Calculating EMI for following Loan Details: Loan Value {}, Interest Rate {}, Loan Term {}",
                loanValue, interestRate, loanTerm);
        var monthlyInterestRate = (interestRate.doubleValue() / MONTHS) / HUNDRED;
        int tenureInMonths = loanTerm > 0 ? (loanTerm * MONTHS) : 1;

        if (monthlyInterestRate == 0.0) {
            return loanValue.divide(BigDecimal.valueOf(tenureInMonths), 2, RoundingMode.UP);
        }

        var powerTerm = Math.pow(1 + monthlyInterestRate, tenureInMonths);
        var emi = (loanValue.doubleValue() * monthlyInterestRate * powerTerm) / (powerTerm - 1);
        return new BigDecimal(emi).setScale(2, RoundingMode.UP);
    }

    @Override
    public Client addLoanToClient(UUID clientId, Loan loan) {
        var client = failIfClientNotPresent(clientId);
        var emi = calculateEmi(loan.loanValue(), loan.interestRate(), loan.loanTerm());

        var loanEntity = loanMapper.toLoanEntity(loan, emi);
        loanEntity.setClient(client);
        loanEntity.setCreatedDate(LocalDate.now());
        client.getLoanEntities().add(loanEntity);

        var updatedClient = clientRepository.save(client);

        return clientMapper.toClientDto(updatedClient);
    }

    @Override
    @Transactional
    public void removeLoanFromClient(UUID clientId, UUID loanId) {
        var client = failIfClientNotPresent(clientId);

        var storedLoan = client.getLoanEntities()
                .stream()
                .filter(loanEntity -> loanEntity.getId().equals(loanId))
                .findFirst()
                .orElseThrow(() -> new LoanNotFoundException(LOAN_NOT_FOUND_CODE,
                        String.format(LOAN_NOT_FOUND_MESSAGE, clientId, loanId)));

        client.getLoanEntities().remove(storedLoan);
        clientRepository.save(client);
    }

    @Override
    @Transactional
    public Client updateClientLoan(UUID clientId, Loan loan) {
        var client = failIfClientNotPresent(clientId);

        var storedLoan = client.getLoanEntities()
                .stream()
                .filter(loanEntity -> loanEntity.getId().equals(loan.loanId()))
                .findFirst()
                .orElseThrow(() -> new LoanNotFoundException(LOAN_NOT_FOUND_CODE,
                        String.format(LOAN_NOT_FOUND_MESSAGE, clientId, loan.loanId())));

        client.getLoanEntities().remove(storedLoan);

        var emi = calculateEmi(loan.loanValue(), loan.interestRate(), loan.loanTerm());

        var loanEntity = loanMapper.toLoanEntity(loan, emi);
        loanEntity.setClient(client);
        loanEntity.setId(storedLoan.getId());
        loanEntity.setCreatedDate(storedLoan.getCreatedDate());

        client.getLoanEntities().add(loanEntity);

        var updatedClient = clientRepository.save(client);

        return clientMapper.toClientDto(updatedClient);

    }

    @Override
    public Client findClientById(UUID clientId) {
        var client = failIfClientNotPresent(clientId);
        return clientMapper.toClientDto(client);
    }

    private ClientEntity failIfClientNotPresent(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(CLIENT_NOT_FOUND_CODE,
                        String.format(CLIENT_NOT_FOUND_MESSAGE, clientId)));
    }
}
