package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractDetails;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.ContractProposalApproval;
import com.intellecteu.onesource.integration.model.onesource.FeeRate;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.RebateRate;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.SettlementInstruction;
import com.intellecteu.onesource.integration.model.onesource.SettlementInstructionUpdate;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.repository.entity.onesource.AgreementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.RerateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementInstructionUpdateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeAgreementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractProposalApprovalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FeeRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FixedRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FloatingRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRebateRateRebateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRerateRateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRerateRerateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfTradeAgreementRateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.PartySettlementInstructionDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RebateRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.SettlementInstructionDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.TradeAgreementDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/*
 * On mapping issues check the OneOf... interface in DTO and remove type or
 * use JsonTypeInfo.Id.DEDUCTION and assign required @JsonSubTypes for OneOf... implementations
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OneSourceMapper {

    public abstract Agreement toModel(AgreementEntity agreementEntity);

    public abstract AgreementEntity toEntity(Agreement agreement);

    public abstract TradeAgreement toModel(TradeAgreementEntity tradeAgreementEntity);

    public abstract TradeAgreementEntity toEntity(TradeAgreement tradeAgreement);

    public abstract Rerate toModel(RerateEntity rerateEntity);

    public abstract Rerate toModel(RerateDTO rerateDTO);

    public abstract ContractDetails toModel(ContractDTO contractDTO);

    public abstract Settlement toModel(PartySettlementInstructionDTO settlementDto);

    public abstract SettlementInstruction toModel(SettlementInstructionDTO instructionDto);

    public abstract Contract toModel(ContractDetails contractDetails);

    public abstract RerateEntity toEntity(Rerate rerate);

    public Rate toModel(OneOfRerateRateDTODTO rateDTO) {
        if (rateDTO instanceof RebateRateDTO) {
            return toModel((RebateRateDTO) rateDTO);
        } else if (rateDTO instanceof FeeRateDTO) {
            return toModel((FeeRateDTO) rateDTO);
        }
        return null;
    }

    public Rate toModel(OneOfRerateRerateDTODTO rerateDTO) {
        if (rerateDTO instanceof RebateRateDTO) {
            return toModel((RebateRateDTO) rerateDTO);
        } else if (rerateDTO instanceof FeeRateDTO) {
            return toModel((FeeRateDTO) rerateDTO);
        }
        return null;
    }

    public abstract Rate toModel(RebateRateDTO rebateRateDTO);

    public abstract Rate toModel(FeeRateDTO rebateRateDTO);

    public RebateRate toModel(OneOfRebateRateRebateDTODTO rebateDTO) {
        if (rebateDTO instanceof FixedRateDTO) {
            return toModel((FixedRateDTO) rebateDTO);
        } else if (rebateDTO instanceof FloatingRateDTO) {
            return toModel((FloatingRateDTO) rebateDTO);
        }
        return null;
    }

    public abstract RebateRate toModel(FixedRateDTO rebateDTO);

    public abstract RebateRate toModel(FloatingRateDTO rebateDTO);

    public abstract Contract toModel(ContractEntity contractEntity);

    public abstract ContractEntity toEntity(Contract contract);

    public abstract Settlement toModel(SettlementEntity settlement);

    public abstract SettlementEntity toEntity(Settlement settlement);

    public abstract List<SettlementEntity> toEntityList(List<Settlement> settlement);

    public abstract List<Settlement> toModelList(List<SettlementEntity> settlement);

    public abstract TradeEvent toModel(TradeEventEntity tradeEventEntity);

    public abstract ContractProposalDTO toRequestDto(ContractProposal contractProposal);

    public abstract ContractProposalApprovalDTO toRequestDto(ContractProposalApproval contractProposalApproval);

    public abstract TradeAgreementDTO toRequestDto(TradeAgreement tradeAgreement);

    public abstract ContractProposal toModel(ContractProposalDTO contractProposal);

    public abstract TradeAgreement toModel(TradeAgreementDTO tradeAgreementDTO);

    public OneOfTradeAgreementRateDTODTO toRequestDto(Rate rateDTO) {
        if (rateDTO.getRebate() != null) {
            return toRequestDto(rateDTO.getRebate());
        } else if (rateDTO.getFee() != null) {
            return toRequestDto(rateDTO.getFee());
        }
        return null;
    }

    public abstract RebateRateDTO toRequestDto(RebateRate rebateRate);

    public abstract FeeRateDTO toRequestDto(FeeRate feeRate);

    public Rate toModel(OneOfTradeAgreementRateDTODTO rateDTO) {
        if (rateDTO instanceof RebateRateDTO) {
            return toModel((RebateRateDTO) rateDTO);
        } else if (rateDTO instanceof FeeRateDTO) {
            return toModel((FeeRateDTO) rateDTO);
        }
        return null;
    }

    public abstract PartySettlementInstructionDTO toRequestDto(Settlement settlement);

    public abstract TradeEventEntity toEntity(TradeEvent tradeEvent);

    public abstract SettlementInstructionUpdateEntity toEntity(SettlementInstructionUpdate settlementInstructionUpdate);

}
