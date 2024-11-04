package com.ademarporto.ls.mapper;

import com.ademarporto.ls.model.Loan;
import com.ademarporto.ls.repository.entity.LoanEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring" ,unmappedTargetPolicy = ReportingPolicy.WARN)
public interface LoanMapper {

    @Mapping(target = "monthlyPayment", source = "emi")
    LoanEntity toLoanEntity(Loan loan, BigDecimal emi);


    @Mapping(target = "loanId", source = "id")
    Loan toLoanDto(LoanEntity loanEntity);

    List<Loan> toLoans(Set<LoanEntity> loanEntities);

    Loan toLoanDto(com.ademarporto.ls.rest.spec.Loan loan);

}
