package com.ademarporto.ls.service;

import com.ademarporto.ls.exception.ClientNotFoundException;
import com.ademarporto.ls.exception.LoanNotFoundException;
import com.ademarporto.ls.mapper.ClientMapper;
import com.ademarporto.ls.mapper.LoanMapper;
import com.ademarporto.ls.model.LoanType;
import com.ademarporto.ls.repository.ClientRepository;
import com.ademarporto.ls.testutils.ClientFactory;
import com.ademarporto.ls.testutils.LoanFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static com.ademarporto.ls.testutils.LoanFactory.EMI_RESULT;
import static com.ademarporto.ls.testutils.LoanFactory.EMI_RESULT_WITHOUT_INTEREST_RATE;
import static com.ademarporto.ls.testutils.LoanFactory.INTEREST_RATE;
import static com.ademarporto.ls.testutils.LoanFactory.LOAN_TERM;
import static com.ademarporto.ls.testutils.LoanFactory.LOAN_VALUE;
import static com.ademarporto.ls.testutils.LoanFactory.createLoanDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("integration")
class LoanServiceIT {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private LoanMapper loanMapper;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private LoanService loanService;

    @Test
    void testCalculateEmi() {
        // When
        var emiResult = loanService.calculateEmi(LOAN_VALUE, INTEREST_RATE, LOAN_TERM);

        // Then
        assertThat(emiResult, is(notNullValue()));
        assertThat(emiResult, is(equalTo(EMI_RESULT)));
    }

    @Test
    void testCalculateEmiWhenInterestRateIsZero() {
        // When
        var emiResult = loanService.calculateEmi(LOAN_VALUE, BigDecimal.ZERO, LOAN_TERM);

        // Then
        assertThat(emiResult, is(notNullValue()));
        assertThat(emiResult, is(equalTo(EMI_RESULT_WITHOUT_INTEREST_RATE)));
    }

    @Test
    void testFindClientById() {
        //Given
        var client = ClientFactory.createClientEntity();
        var loanEntity = LoanFactory.createLoanEntity();
        loanEntity.setClient(client);
        client.setLoanEntities(Set.of(loanEntity));
        var storedClient = clientRepository.save(client);

        //When
        var response = loanService.findClientById(storedClient.getId());

        // Then
        assertThat(response, is(notNullValue()));
        assertFalse(response.loans().isEmpty());
        assertThat(response.clientId(), is(equalTo(storedClient.getId())));
    }

    @Test
    void testAddLoanToClient() {
        //Given
        var client = ClientFactory.createClientEntity();
        var storedClient = clientRepository.save(client);
        var loan = createLoanDto(null, LoanType.HOME);

        //When
        var response = loanService.addLoanToClient(storedClient.getId(), loan);

        // Then
        assertThat(response, is(notNullValue()));
        assertThat(response.loans(), is(notNullValue()));
    }

    @Test
    void testAddLoanToClientException() {
        //Given
        var clientId = UUID.randomUUID();
        var loan = createLoanDto(null, LoanType.HOME);

        //When && Then
        assertThrows(ClientNotFoundException.class, () -> loanService.addLoanToClient(clientId, loan));
    }

    @Test
    void testRemoveLoanFromClient() {
        //Given
        var client = ClientFactory.createClientEntity();
        var loanEntity = LoanFactory.createLoanEntity();
        loanEntity.setClient(client);
        client.setLoanEntities(Set.of(loanEntity));
        var storedClient = clientRepository.save(client);

        var storedLoan = storedClient.getLoanEntities().iterator().next();

        //When
        loanService.removeLoanFromClient(storedClient.getId(), storedLoan.getId());
        var result = clientRepository.findById(storedClient.getId());

        // Then
        assertThat(result, is(notNullValue()));
        assertTrue(result.isPresent());
        assertThat(result.get().getLoanEntities(), is(empty()));
    }

    @Test
    void testRemoveLoanFromClientException() {
        //Given
        var clientId = UUID.randomUUID();
        var loanId = UUID.randomUUID();

        //When && Then
        assertThrows(ClientNotFoundException.class, () -> loanService.removeLoanFromClient(clientId, loanId));
    }

    @Test
    void testRemoveLoanFromClientLoanNotFoundException() {
        //Given
        var client = ClientFactory.createClientEntity();
        var loanId = UUID.randomUUID();

        var storedClient = clientRepository.save(client);

        //When && Then
        assertThrows(LoanNotFoundException.class, () -> loanService.removeLoanFromClient(storedClient.getId(), loanId));

    }

    @Test
    void testUpdateClientLoan() {
        //Given
        var client = ClientFactory.createClientEntity();
        var loanEntity = LoanFactory.createLoanEntity();
        loanEntity.setClient(client);
        client.setLoanEntities(Set.of(loanEntity));
        var storedClient = clientRepository.save(client);
        var storedLoan = storedClient.getLoanEntities().iterator().next();

        var updatedLoan = LoanFactory.createLoanDto(storedLoan.getId(), LoanType.PERSONAL);
        //When
        var response = loanService.updateClientLoan(storedClient.getId(), updatedLoan);

        // Then
        assertThat(response, is(notNullValue()));
        assertFalse(response.loans().isEmpty());
        var loan = response.loans().get(0);
        assertThat(loan.loanId(), is(equalTo(storedLoan.getId())));
        assertThat(loan.loanType(), is(equalTo(LoanType.PERSONAL)));
    }

    @Test
    void testUpdateClientLoanLoanNotFoundException() {
        //Given
        var client = ClientFactory.createClientEntity();
        var storedClient = clientRepository.save(client);
        var loanId = UUID.randomUUID();
        var updatedLoan = LoanFactory.createLoanDto(loanId, LoanType.PERSONAL);

        //When && Then
        assertThrows(LoanNotFoundException.class, () -> loanService.updateClientLoan(storedClient.getId(), updatedLoan));
    }
}
