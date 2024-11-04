package com.ademarporto.ls.mapper;

import com.ademarporto.ls.model.Client;
import com.ademarporto.ls.repository.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN,
        uses = LoanMapper.class)
public interface ClientMapper {

    @Mapping(target = "loans", source = "loanEntities")
    @Mapping(target = "clientId", source = "id")
    Client toClientDto(ClientEntity clientEntity);

    com.ademarporto.ls.rest.spec.Client toClientSpec(Client client);
}
