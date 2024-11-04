package com.ademarporto.ls.testutils;

import com.ademarporto.ls.repository.entity.ClientEntity;
import com.ademarporto.ls.rest.spec.LoanRequest;
import com.ademarporto.ls.rest.spec.LoanType;

import java.time.LocalDate;
import java.util.UUID;

public class ClientFactory {

    public static final UUID CLIENT_ID = UUID.fromString("3e8ee769-1f13-4bec-a5d3-fb7b20007883");
    public static final String CLIENT_NAME = "Test Client";
    public static final LocalDate CREATED_DATE = LocalDate.parse("2024-11-04");

    private ClientFactory() {
    }

    public static ClientEntity createClientEntity() {
        var client = new ClientEntity();
        client.setName(CLIENT_NAME);
        client.setCreatedDate(CREATED_DATE);
        return client;
    }

    public static LoanRequest createLoanRequest(UUID clientId) {
        var request = new LoanRequest();
        request.setClientId(clientId);
        request.setLoan(LoanFactory.createLoanSpec());
        return request;
    }

    public static LoanRequest updateLoanRequest(UUID clientId, UUID loanId, LoanType loanType) {
        var request = new LoanRequest();
        request.setClientId(clientId);
        request.setLoan(LoanFactory.createLoanSpec(loanId, loanType));
        return request;
    }
}
