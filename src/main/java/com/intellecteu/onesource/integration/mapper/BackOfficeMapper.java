package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.backoffice.TradeOut;
import com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity;
import com.intellecteu.onesource.integration.repository.entity.backoffice.RerateTradeEntity;
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.TradeOutDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
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

    @Mapping(source = "account", target = "positionAccount")
    @Mapping(source = "counterParty", target = "positionCpAccount")
    public abstract Position toModel(PositionEntity positionEntity);

    @Mapping(target = "account", source = "positionAccount")
    @Mapping(target = "counterParty", source = "positionCpAccount")
    public abstract PositionEntity toEntity(Position position);

    public abstract RerateTrade toModel(RerateTradeEntity rerateTradeEntity);

    public abstract RerateTradeEntity toEntity(RerateTrade rerateTrade);

    @Mapping(target = "positionId", source = "positionOutDTO.positionId")
    @Mapping(target = "tradeId", source = "tradeId")
    @Mapping(target = "positionType.positionTypeId", source = "positionOutDTO.positiontypeDTO.positionTypeId")
    @Mapping(target = "positionType.positionType", source = "positionOutDTO.positiontypeDTO.positionType")
    @Mapping(target = "positionSecurityId", source = "positionOutDTO.securityId")
    @Mapping(target = "customValue2", source = "positionOutDTO.customValue2")
    @Mapping(target = "positionStatus.statusId", source = "positionOutDTO.statusDTO.statusId")
    @Mapping(target = "positionStatus.status", source = "positionOutDTO.statusDTO.status")
    @Mapping(target = "positionSecurityDetail", source = "positionOutDTO.securityDetailDTO")
    @Mapping(target = "currency", source = "positionOutDTO.currencyDTO")
    @Mapping(target = "rate", source = "positionOutDTO.rate")
    @Mapping(target = "deliverFree", source = "positionOutDTO.deliverFree")
    @Mapping(target = "termId", source = "positionOutDTO.termId")
    @Mapping(target = "endDate", source = "positionOutDTO.endDate")
    @Mapping(target = "index", source = "positionOutDTO.indexDTO")
    @Mapping(target = "positionCollateralType", source = "positionOutDTO.collateralTypeDTO")
    @Mapping(target = "loanBorrow", source = "positionOutDTO.loanBorrowDTO")
    @Mapping(target = "exposure", source = "positionOutDTO.exposureDTO")
    @Mapping(target = "positionAccount", source = "positionOutDTO.accountDTO")
    @Mapping(target = "positionCpAccount", source = "positionOutDTO.counterPartyDTO")
    public abstract Position toPositionModel(TradeOutDTO tradeOutDTO);


}
