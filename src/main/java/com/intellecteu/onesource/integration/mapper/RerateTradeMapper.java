package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.model.backoffice.spire.Position;
import com.intellecteu.onesource.integration.model.backoffice.spire.TradeOut;
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.TradeOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class RerateTradeMapper {

    @Mapping(target = "tradeType", source = "tradeOutDTO.tradeTypeDetailDTO.tradeType")
    @Mapping(target = "tradeTypeId", source = "tradeOutDTO.tradeTypeDetailDTO.tradeTypeId")
    @Mapping(target = "position", source = "tradeOutDTO.positionOutDTO")
    public abstract TradeOut toModel(TradeOutDTO tradeOutDTO);

    @Mapping(target = "positionSecurityDetail", source = "securityDetailDTO")
    @Mapping(target = "currency", source = "currencyDTO")
    @Mapping(target = "loanBorrow", source = "loanBorrowDTO")
    @Mapping(target = "positionCollateralType", source = "collateralTypeDTO")
    @Mapping(target = "exposure", source = "exposureDTO")
    @Mapping(target = "positionType", source = "positiontypeDTO")
    @Mapping(target = "index", source = "indexDTO")
    @Mapping(target = "positionAccount", source = "accountDTO")
    @Mapping(target = "positionCpAccount", source = "counterPartyDTO")
    @Mapping(target = "positionStatus", source = "statusDTO")
    public abstract Position toModel(PositionOutDTO positionOutDTO);

}
