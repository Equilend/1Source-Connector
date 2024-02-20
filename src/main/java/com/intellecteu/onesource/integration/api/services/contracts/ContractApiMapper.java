package com.intellecteu.onesource.integration.api.services.contracts;

import com.intellecteu.onesource.integration.api.dto.ContractDto;
import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface ContractApiMapper {

    ContractDto toDto(ContractEntity entity);

}
