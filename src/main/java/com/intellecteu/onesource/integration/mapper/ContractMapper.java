package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    ContractEntity toEntity(Contract contract);

    Contract toModel(ContractEntity contractEntity);

    SettlementEntity toEntity(Settlement settlement);

    Settlement toModel(SettlementEntity settlementEntity);

}
