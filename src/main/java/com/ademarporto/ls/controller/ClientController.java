package com.ademarporto.ls.controller;

import com.ademarporto.ls.mapper.ClientMapper;
import com.ademarporto.ls.mapper.LoanMapper;
import com.ademarporto.ls.rest.api.ClientControllerApi;
import com.ademarporto.ls.rest.spec.Client;
import com.ademarporto.ls.rest.spec.ClientResponse;
import com.ademarporto.ls.rest.spec.LoanRequest;
import com.ademarporto.ls.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientController implements ClientControllerApi {

    private final LoanService loanService;
    private final ClientMapper clientMapper;
    private final LoanMapper loanMapper;

    @Override
    public ResponseEntity<Client> getClientById(@PathVariable("client-id") UUID clientId ) {
        log.info("Hit [getClientById] endpoint.");

        var client = loanService.findClientById(clientId);
        var response = clientMapper.toClientSpec(client);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ClientResponse> addLoanToClient(@Valid @RequestBody LoanRequest loanRequest) {
        log.info("Hit [addLoanToClient] endpoint.");

        var loan = loanMapper.toLoanDto(loanRequest.getLoan());
        var client = loanService.addLoanToClient(loanRequest.getClientId(), loan);

        var response = new ClientResponse();
        response.setClient(clientMapper.toClientSpec(client));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> removeLoanFromClient(@PathVariable("client-id") UUID clientId,
                                                     @PathVariable("loan-id") UUID loanId) {
        log.info("Hit [removeLoanFromClient] endpoint.");
        loanService.removeLoanFromClient(clientId, loanId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ClientResponse> updateClientLoan(@Valid @RequestBody LoanRequest loanRequest) {
        log.info("Hit [updateClientLoan] endpoint.");
        var loan = loanMapper.toLoanDto(loanRequest.getLoan());
        var client = loanService.updateClientLoan(loanRequest.getClientId(), loan);

        var response = new ClientResponse();
        response.setClient(clientMapper.toClientSpec(client));

        return ResponseEntity.ok(response);
    }
}
