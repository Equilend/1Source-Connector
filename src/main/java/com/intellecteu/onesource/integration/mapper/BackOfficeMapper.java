package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.kafka.dto.RecallInstructionDTO;
import com.intellecteu.onesource.integration.model.backoffice.Account;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.PositionConfirmationRequest;
import com.intellecteu.onesource.integration.model.backoffice.PositionInstruction;
import com.intellecteu.onesource.integration.model.backoffice.RecallSpire;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.model.backoffice.TradeOut;
import com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity;
import com.intellecteu.onesource.integration.repository.entity.backoffice.RecallSpireEntity;
import com.intellecteu.onesource.integration.repository.entity.backoffice.RerateTradeEntity;
import com.intellecteu.onesource.integration.repository.entity.backoffice.ReturnTradeEntity;
import com.intellecteu.onesource.integration.services.client.spire.dto.AccountDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.OneSourceConfimationDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.TradeOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.instruction.InstructionDTO;
import java.util.List;
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
    @Mapping(target = "status", source = "tradeOutDTO.statusDTO.status")
    @Mapping(target = "statusId", source = "tradeOutDTO.statusDTO.statusId")
    @Mapping(target = "account", source = "tradeOutDTO.accountDTO")
    @Mapping(target = "counterParty", source = "tradeOutDTO.counterPartyDTO")
    @Mapping(target = "depoId", source = "tradeOutDTO.depositoryDTO.depoId")
    @Mapping(target = "depoKy", source = "tradeOutDTO.depositoryDTO.depoKy")
    public abstract TradeOut toModel(TradeOutDTO tradeOutDTO);

    @Mapping(target = "positionSecurityDetail", source = "securityDetailDTO")
    @Mapping(target = "currency", source = "currencyDTO")
    @Mapping(target = "loanBorrow", source = "loanBorrowDTO")
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

    public abstract List<Position> toPositionList(List<PositionEntity> positionEntities);

    @Mapping(target = "account", source = "positionAccount")
    @Mapping(target = "counterParty", source = "positionCpAccount")
    public abstract PositionEntity toEntity(Position position);

    public abstract RerateTrade toModel(RerateTradeEntity rerateTradeEntity);

    public abstract RerateTradeEntity toEntity(RerateTrade rerateTrade);

    @Mapping(target = "positionId", source = "positionOutDTO.positionId")
    @Mapping(target = "tradeId", source = "tradeId")
    @Mapping(target = "positionType.positionTypeId", source = "positionOutDTO.positiontypeDTO.positionTypeId")
    @Mapping(target = "positionType.positionType", source = "positionOutDTO.positiontypeDTO.positionType")
    @Mapping(target = "positionType.isCash", source = "positionOutDTO.positiontypeDTO.isCash")
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
    @Mapping(target = "loanBorrow", source = "positionOutDTO.loanBorrowDTO")
    @Mapping(target = "exposure", source = "positionOutDTO.exposureDTO")
    @Mapping(target = "positionAccount", source = "positionOutDTO.accountDTO")
    @Mapping(target = "positionCpAccount", source = "positionOutDTO.counterPartyDTO")
    public abstract Position toPositionModel(TradeOutDTO tradeOutDTO);

    public abstract OneSourceConfimationDTO toRequestDto(PositionConfirmationRequest confirmationRequest);

    @Mapping(target = "accountDTO", source = "account")
    public abstract InstructionDTO toRequestDto(PositionInstruction positionInstruction);

    public abstract AccountDTO toRequestDto(Account positionAccount);

    public abstract ReturnTrade toModel(ReturnTradeEntity returnTradeEntity);

    public abstract ReturnTradeEntity toEntity(ReturnTrade returnTrade);

    @Mapping(target = "lastUpdateDateTime", ignore = true)
    public abstract RecallSpireEntity toEntity(RecallSpire recallSpire);

    public abstract RecallSpire toModel(RecallSpireEntity recallSpireEntity);

    @Mapping(target = "recallId", source = "spireRecallId")
    public abstract RecallSpire toModel(RecallInstructionDTO recallInstructionDTO);

}
