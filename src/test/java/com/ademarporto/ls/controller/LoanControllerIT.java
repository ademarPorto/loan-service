package com.ademarporto.ls.controller;

import com.ademarporto.ls.rest.spec.LoanCalculationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.ademarporto.ls.testutils.LoanFactory.INTEREST_RATE;
import static com.ademarporto.ls.testutils.LoanFactory.LOAN_TERM;
import static com.ademarporto.ls.testutils.LoanFactory.LOAN_VALUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
public class LoanControllerIT {

    private static final String CALCULATE_LOAN_ENDPOINT = "/v1/loan/calculate";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoanController loanController;

    @Test
    void when_AllParametersPresent_ThenReturn_CalculatedEMI() throws Exception {
        var mvcResult = mockMvc
                .perform(get(CALCULATE_LOAN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("loan-value", String.valueOf(LOAN_VALUE))
                        .queryParam("interest-rate", String.valueOf(INTEREST_RATE))
                        .queryParam("loan-term", String.valueOf(LOAN_TERM)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var response = objectMapper.readValue(mvcResult, LoanCalculationResponse.class);


        assertThat(response, is(notNullValue()));
        assertThat(response.getEmi(), is(notNullValue()));
        assertThat(response.getEmi(), is(equalTo(BigDecimal.valueOf(964.92))));
    }

    @Test
    void when_LoanValueNotPresent_ThenReturn_Exception() throws Exception {
        mockMvc.perform(get(CALCULATE_LOAN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("interest-rate", String.valueOf(INTEREST_RATE))
                        .queryParam("loan-term", String.valueOf(LOAN_TERM)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'loan-value' is not present."));
    }

    @Test
    void when_InterestRateNotPresent_ThenReturn_Exception() throws Exception {
        mockMvc.perform(get(CALCULATE_LOAN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("loan-value", String.valueOf(LOAN_VALUE))
                        .queryParam("loan-term", String.valueOf(LOAN_TERM)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'interest-rate' is not present."));
    }

    @Test
    void when_LoanTermNotPresent_ThenReturn_Exception() throws Exception {
        mockMvc.perform(get(CALCULATE_LOAN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("loan-value", String.valueOf(LOAN_VALUE))
                        .queryParam("interest-rate", String.valueOf(INTEREST_RATE)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'loan-term' is not present."));
    }

    @Test
    void when_InvalidLoanValue_ThenReturn_Exception() throws Exception {
        mockMvc.perform(get(CALCULATE_LOAN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("loan-value", String.valueOf(-1))
                        .queryParam("interest-rate", String.valueOf(INTEREST_RATE))
                        .queryParam("loan-term", String.valueOf(LOAN_TERM)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("calculateEmi.loanValue: must be greater than or equal to 0"));
    }

    @Test
    void when_InterestRateLessThanZero_ThenReturn_Exception() throws Exception {
        mockMvc.perform(get(CALCULATE_LOAN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("loan-value", String.valueOf(LOAN_VALUE))
                        .queryParam("interest-rate", String.valueOf(-1))
                        .queryParam("loan-term", String.valueOf(LOAN_TERM)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("calculateEmi.interestRate: must be greater than or equal to 0"));
    }

    @Test
    void when_InterestRateGreaterThanOneHundred_ThenReturn_Exception() throws Exception {
        mockMvc.perform(get(CALCULATE_LOAN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("loan-value", String.valueOf(LOAN_VALUE))
                        .queryParam("interest-rate", String.valueOf(101))
                        .queryParam("loan-term", String.valueOf(LOAN_TERM)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("calculateEmi.interestRate: must be less than or equal to 100"));
    }

    @Test
    void when_LoanTermLessThanZero_ThenReturn_Exception() throws Exception {
        mockMvc.perform(get(CALCULATE_LOAN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("loan-value", String.valueOf(LOAN_VALUE))
                        .queryParam("interest-rate", String.valueOf(INTEREST_RATE))
                        .queryParam("loan-term", String.valueOf(-1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("calculateEmi.loanTerm: must be greater than or equal to 0"));
    }

    @Test
    void when_LoanTermGreaterThanThirty_ThenReturn_Exception() throws Exception {
        mockMvc.perform(get(CALCULATE_LOAN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("loan-value", String.valueOf(LOAN_VALUE))
                        .queryParam("interest-rate", String.valueOf(INTEREST_RATE))
                        .queryParam("loan-term", String.valueOf(31)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("calculateEmi.loanTerm: must be less than or equal to 30"));
    }
}
