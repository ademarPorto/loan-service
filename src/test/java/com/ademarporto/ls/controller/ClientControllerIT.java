package com.ademarporto.ls.controller;


import com.ademarporto.ls.repository.ClientRepository;
import com.ademarporto.ls.repository.entity.ClientEntity;
import com.ademarporto.ls.rest.spec.Client;
import com.ademarporto.ls.rest.spec.ClientResponse;
import com.ademarporto.ls.rest.spec.LoanType;
import com.ademarporto.ls.testutils.ClientFactory;
import com.ademarporto.ls.testutils.LoanFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
public class ClientControllerIT {

    private static final String LOAN_TO_CLIENT_ENDPOINT = "/v1/client/loan";
    private static final String GET_CLIENT_BY_ID_ENDPOINT = "/v1/clients/";
    private static final String REMOVE_LOAN_FROM_CLIENT_ENDPOINT = "/v1/client/%s/loan/%s";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientController clientController;

    @Autowired
    private ClientRepository clientRepository;

    private ClientEntity clientEntity;

    @BeforeEach
    void setup() {
        var client = ClientFactory.createClientEntity();
        clientEntity = clientRepository.save(client);
    }

    @Test
    void when_AddLoanToClient_ThenReturn_Created() throws Exception {
        var request = ClientFactory.createLoanRequest(clientEntity.getId());
        var mvcResult = mockMvc
                .perform(post(LOAN_TO_CLIENT_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var response = objectMapper.readValue(mvcResult, ClientResponse.class);


        assertThat(response, is(notNullValue()));
        assertThat(response.getClient().getClientId(), is(equalTo(request.getClientId())));
        assertThat(response.getClient().getLoans(), is(notNullValue()));
    }

    @Test
    void when_UpdateClientLoan_ThenReturn_Ok() throws Exception {
        var client = createClientWithLoan();
        var loan = client.getLoanEntities().iterator().next();

        var request = ClientFactory.updateLoanRequest(client.getId(), loan.getId(), LoanType.PERSONAL);
        var mvcResult = mockMvc
                .perform(put(LOAN_TO_CLIENT_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var response = objectMapper.readValue(mvcResult, ClientResponse.class);


        assertThat(response, is(notNullValue()));
        assertThat(response.getClient().getClientId(), is(equalTo(request.getClientId())));
        assertThat(response.getClient().getLoans().getFirst(), is(notNullValue()));
        var updatedLoan = response.getClient().getLoans().getFirst();
        assertThat(updatedLoan.getLoanType(), is(equalTo(LoanType.PERSONAL)));
    }

    @Test
    void when_GetClientById_ThenReturn_Ok() throws Exception {
        var client = createClientWithLoan();

        var mvcResult = mockMvc
                .perform(get(GET_CLIENT_BY_ID_ENDPOINT + client.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var response = objectMapper.readValue(mvcResult, Client.class);

        assertThat(response, is(notNullValue()));
        assertThat(response.getClientId(), is(equalTo(client.getId())));
    }

    @Test
    void when_RemoveLoanFromClient_ThenReturn_NoContent() throws Exception {
        var client = createClientWithLoan();
        var loan = client.getLoanEntities().iterator().next();

        mockMvc
                .perform(delete(String.format(REMOVE_LOAN_FROM_CLIENT_ENDPOINT, client.getId(), loan.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        var result = clientRepository.findById(client.getId());

        assertThat(result, is(notNullValue()));
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getLoanEntities(), is(empty()));
    }

    @Transactional
    private ClientEntity createClientWithLoan() {
        var client = ClientFactory.createClientEntity();
        var loan = LoanFactory.createLoanEntity();
        loan.setClient(client);
        client.setLoanEntities(Set.of(loan));
        return clientRepository.save(client);
    }
}
