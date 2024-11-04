package com.ademarporto.ls.controller;

import com.ademarporto.ls.rest.api.LoanControllerApi;
import com.ademarporto.ls.rest.spec.LoanCalculationResponse;
import com.ademarporto.ls.service.LoanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoanController implements LoanControllerApi {

    private final LoanService loanService;

    @Override
    public ResponseEntity<LoanCalculationResponse> calculateEmi(
            @NotNull  @DecimalMin("0") @Valid @RequestParam(value = "loan-value") BigDecimal loanValue,
            @NotNull @DecimalMin("0") @DecimalMax("100") @Valid
            @RequestParam(value = "interest-rate") BigDecimal interestRate,
            @NotNull @Min(0) @Max(30) @Valid @RequestParam(value = "loan-term") Integer loanTerm) {
        log.info("Hit [calculateEmi] endpoint.");

        var emiResult = loanService.calculateEmi(loanValue, interestRate, loanTerm);

        var response = new LoanCalculationResponse();
        response.setEmi(emiResult);
        return ResponseEntity.ok(response);
    }
}
