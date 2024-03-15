package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.RebateRate;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
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
import com.intellecteu.onesource.integration.services.client.onesource.dto.FeeRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FixedRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FloatingRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRebateRateRebateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRerateRateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRerateRerateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RebateRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OneSourceMapper {

    public abstract Agreement toModel(AgreementEntity agreementEntity);

    public abstract AgreementEntity toEntity(Agreement agreement);

    public abstract TradeAgreement toModel(TradeAgreementEntity tradeAgreementEntity);

    public abstract TradeAgreementEntity toEntity(TradeAgreement tradeAgreement);

    public abstract Rerate toModel(RerateEntity rerateEntity);

    public abstract Rerate toModel(RerateDTO rerateDTO);

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

    public abstract TradeEventEntity toEntity(TradeEvent tradeEvent);

    public abstract SettlementInstructionUpdateEntity toEntity(SettlementInstructionUpdate settlementInstructionUpdate);

}
