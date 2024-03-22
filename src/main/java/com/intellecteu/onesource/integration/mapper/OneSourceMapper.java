package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Collateral;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.FeeRate;
import com.intellecteu.onesource.integration.model.onesource.FixedRate;
import com.intellecteu.onesource.integration.model.onesource.FloatingRate;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.RebateRate;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.RerateStatus;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.SettlementInstruction;
import com.intellecteu.onesource.integration.model.onesource.SettlementInstructionUpdate;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.model.onesource.Venue;
import com.intellecteu.onesource.integration.repository.entity.onesource.AgreementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.CollateralEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.FeeRateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.FixedRateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.FloatingRateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.RateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.RebateRateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.RerateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementInstructionEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementInstructionUpdateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeAgreementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.VenueEntity;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FeeRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FixedRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FloatingRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRebateRateRebateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRerateRateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRerateRerateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RebateRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateStatusDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class OneSourceMapper {

    public abstract Agreement toModel(AgreementEntity agreementEntity);

    public abstract AgreementEntity toEntity(Agreement agreement);

    public abstract TradeAgreement toModel(TradeAgreementEntity tradeAgreementEntity);

    public abstract TradeAgreementEntity toEntity(TradeAgreement tradeAgreement);

    public abstract Rerate toModel(RerateEntity rerateEntity);

    @Mapping(target = "rerateStatus", source = "status")
    public abstract Rerate toModel(RerateDTO rerateDTO);

    public abstract RerateStatus mapStatus(RerateStatusDTO rerateStatusDTO);

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

    public abstract TradeEvent toModel(TradeEventEntity tradeEventEntity);

    public abstract TradeEventEntity toEntity(TradeEvent tradeEvent);

    public abstract Settlement toModel(SettlementEntity settlementEntity);

    public abstract SettlementEntity toEntity(Settlement settlement);

    public abstract SettlementInstructionUpdateEntity toEntity(SettlementInstructionUpdate settlementInstructionUpdate);

}
