package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.PositionSecurityDetail;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.backoffice.TradeOut;
import com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity;
import com.intellecteu.onesource.integration.repository.entity.backoffice.RerateTradeEntity;
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.TradeOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class BackOfficeMapper {

    @Mapping(target = "tradeType", source = "tradeOutDTO.tradeTypeDetailDTO.tradeType")
    @Mapping(target = "tradeTypeId", source = "tradeOutDTO.tradeTypeDetailDTO.tradeTypeId")
    @Mapping(target = "position", source = "tradeOutDTO.positionOutDTO")
    @Mapping(target = "index", source = "tradeOutDTO.positionOutDTO.indexDTO")
    @Mapping(target = "status", source = "tradeOutDTO.positionOutDTO.statusDTO.status")
    @Mapping(target = "statusId", source = "tradeOutDTO.positionOutDTO.statusDTO.statusId")
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

    public abstract Position toModel(PositionEntity positionEntity);

    public abstract PositionEntity toEntity(Position position);

    public abstract RerateTrade toModel(RerateTradeEntity rerateTradeEntity);

    public abstract RerateTradeEntity toEntity(RerateTrade rerateTrade);

}
