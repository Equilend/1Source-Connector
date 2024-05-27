package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractDetails;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.ContractProposalApproval;
import com.intellecteu.onesource.integration.model.onesource.FeeRate;
import com.intellecteu.onesource.integration.model.onesource.Instrument;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.RebateRate;
import com.intellecteu.onesource.integration.model.onesource.Recall1Source;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.RerateStatus;
import com.intellecteu.onesource.integration.model.onesource.Return;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.SettlementInstruction;
import com.intellecteu.onesource.integration.model.onesource.SettlementInstructionUpdate;
import com.intellecteu.onesource.integration.model.onesource.SettlementStatusUpdate;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.model.onesource.Venue;
import com.intellecteu.onesource.integration.model.onesource.VenueParty;
import com.intellecteu.onesource.integration.repository.entity.onesource.AgreementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.InstrumentEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.Recall1SourceEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.RerateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.ReturnEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementInstructionUpdateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeAgreementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.VenueEntity;
import com.intellecteu.onesource.integration.services.client.onesource.dto.AgreementDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractProposalApprovalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.EventDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FeeRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FixedRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FloatingRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.InstrumentDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRebateRateRebateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRerateRateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRerateRerateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfTradeAgreementRateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfVenueTradeAgreementRateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.PartySettlementInstructionDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RebateRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RecallDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateStatusDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ReturnDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.SettlementInstructionDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.SettlementStatusUpdateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.TradeAgreementDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenueDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenuePartyDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenueTradeAgreementDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mapstruct.AfterMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/*
 * On mapping issues check the OneOf... interface in DTO and remove type or
 * use JsonTypeInfo.Id.DEDUCTION and assign required @JsonSubTypes for OneOf... implementations
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OneSourceMapper {


    public abstract Agreement toModel(AgreementEntity agreementEntity);

    @Mapping(target = "venues", qualifiedByName = "toVenueList")
    public abstract TradeAgreement toModel(TradeAgreementEntity agreementEntity);

    @IterableMapping(qualifiedByName = "toVenueModel")
    @Named("toVenueList")
    public abstract List<Venue> toVenueList(List<VenueEntity> venueEntities);

    @Named("toVenueModel")
    public abstract Venue toVenueModel(VenueEntity venueEntity);

    public abstract VenueEntity toModel(Venue venueModel);

    @AfterMapping
    public void setTradeId(Object anySource, @MappingTarget TradeAgreement tradeAgreement) {
        Optional.ofNullable(tradeAgreement.getVenues())
            .ifPresent(venues -> venues.forEach(venue -> venue.setTradeId(tradeAgreement.getId())));
    }

    public abstract AgreementEntity toEntity(Agreement agreement);

    @Mapping(target = "lastUpdateDateTime", source = "lastUpdateDatetime")
    public abstract Agreement toModel(AgreementDTO agreementDTO);

    public abstract AgreementDTO toModel(Agreement agreement);

    @Mapping(target = "venues", source = "executionVenue", qualifiedByName = "toVenueModelFromExecutionVenue")
    public abstract TradeAgreement toModel(VenueTradeAgreementDTO venueTradeAgreementDTO);

    public abstract VenueTradeAgreementDTO toModel(TradeAgreement tradeAgreement);

    @Named("toVenueModelFromExecutionVenue")
    public List<Venue> toModel(VenueDTO executionVenue) {
        Venue venue = toVenueModel(executionVenue);
        List<Venue> venueList = new ArrayList<>();
        venueList.add(venue);
        return venueList;
    }

    @Mapping(target = "quickCode", source = "quick")
    public abstract Instrument toModel(InstrumentDTO instrumentDTO);

    public abstract VenueParty toModel(VenuePartyDTO venuePartyDTO);

    @Mapping(target = "transactionDateTime", source = "transactionDatetime")
    public abstract Venue toVenueModel(VenueDTO venueDTO);

    @Mapping(target = "quickCode", source = "quick")
    public abstract Instrument toModel(InstrumentEntity instrumentEntity);

    @Mapping(target = "quick", source = "quickCode")
    public abstract InstrumentEntity toEntity(Instrument tradeAgreement);

    public abstract TradeAgreementEntity toEntity(TradeAgreement tradeAgreement);

    public abstract Rerate toModel(RerateEntity rerateEntity);

    @Mapping(target = "rerateStatus", source = "status")
    public abstract Rerate toModel(RerateDTO rerateDTO);

    public abstract ContractDetails toModel(ContractDTO contractDTO);

    public abstract Settlement toModel(PartySettlementInstructionDTO settlementDto);

    public abstract SettlementInstruction toModel(SettlementInstructionDTO instructionDto);

    public abstract Contract toModel(ContractDetails contractDetails);

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

    public abstract Settlement toModel(SettlementEntity settlement);

    public abstract SettlementEntity toEntity(Settlement settlement);

    public abstract List<SettlementEntity> toEntityList(List<Settlement> settlement);

    public abstract List<Settlement> toModelList(List<SettlementEntity> settlement);

    @Mapping(target = "eventDateTime", source = "eventDateTime")
    public abstract TradeEvent toModel(EventDTO eventDTO);

    public abstract List<TradeEvent> toTradeEventModelList(List<EventDTO> eventsDTO);

    public abstract TradeEvent toModel(TradeEventEntity tradeEventEntity);

    @Mapping(target = "settlement", source = "settlementList")
    public abstract ContractProposalDTO toRequestDto(ContractProposal contractProposal);

    public abstract ContractProposalApprovalDTO toRequestDto(ContractProposalApproval contractProposalApproval);

    public abstract TradeAgreementDTO toRequestDto(TradeAgreement tradeAgreement);

    public abstract ContractProposal toModel(ContractProposalDTO contractProposal);

    public abstract TradeAgreement toModel(TradeAgreementDTO tradeAgreementDTO);

    public OneOfVenueTradeAgreementRateDTODTO toVenueTradeRequestDto(Rate rate) {
        if (rate.getRebate() != null) {
            return toRequestDto(rate.getRebate());
        } else if (rate.getFee() != null) {
            return toRequestDto(rate.getFee());
        }
        return null;
    }

    public OneOfTradeAgreementRateDTODTO toRequestDto(Rate rate) {
        if (rate.getRebate() != null) {
            return toRequestDto(rate.getRebate());
        } else if (rate.getFee() != null) {
            return toRequestDto(rate.getFee());
        }
        return null;
    }

    public RebateRateDTO toRequestDto(RebateRate rebateRate) {
        if (rebateRate.getFixed() != null) {
            RebateRateDTO rebateRateDTO = new RebateRateDTO();
            FixedRateDTO fixedRateDto = toFixedRebateDto(rebateRate);
            rebateRateDTO.setRebate(fixedRateDto);
            return rebateRateDTO;
        }
        if (rebateRate.getFloating() != null) {
            RebateRateDTO rebateRateDTO = new RebateRateDTO();
            FloatingRateDTO floatingRateDTO = toFloatingRebateDto(rebateRate);
            rebateRateDTO.setRebate(floatingRateDTO);
            return rebateRateDTO;
        }
        return null;
    }

    public abstract FeeRateDTO toRequestDto(FeeRate feeRate);

    public abstract FloatingRateDTO toFloatingRebateDto(RebateRate rebateRate);

    public abstract FixedRateDTO toFixedRebateDto(RebateRate rebateRate);

    public Rate toModel(OneOfVenueTradeAgreementRateDTODTO rateDTO) {
        if (rateDTO instanceof RebateRateDTO) {
            return toModel((RebateRateDTO) rateDTO);
        } else if (rateDTO instanceof FeeRateDTO) {
            return toModel((FeeRateDTO) rateDTO);
        }
        return null;
    }

    public Rate toModel(OneOfTradeAgreementRateDTODTO rateDTO) {
        if (rateDTO instanceof RebateRateDTO) {
            return toModel((RebateRateDTO) rateDTO);
        } else if (rateDTO instanceof FeeRateDTO) {
            return toModel((FeeRateDTO) rateDTO);
        }
        return null;
    }

    public abstract SettlementStatusUpdateDTO toRequestDto(SettlementStatusUpdate feeRate);

    public abstract PartySettlementInstructionDTO toRequestDto(Settlement settlement);

    public abstract TradeEventEntity toEntity(TradeEvent tradeEvent);

    public abstract SettlementInstructionUpdateEntity toEntity(SettlementInstructionUpdate settlementInstructionUpdate);

    @Mapping(target = "returnStatus", source = "status")
    public abstract Return toModel(ReturnDTO returnDTO);

    public abstract Return toModel(ReturnEntity returnEntity);

    public abstract ReturnEntity toEntity(Return oneSourceEntity);


    @Named("mapIntegerToDouble")
    public Double mapIntegerToDouble(Integer integer) {
        if (integer == null) {
            return null;
        }
        return integer.doubleValue();
    }

    @Mapping(target = "lastUpdateDateTime", ignore = true)
    public abstract Recall1SourceEntity toEntity(Recall1Source recall1Source);

    public abstract Recall1Source toModel(Recall1SourceEntity recall1SourceEntity);

    @Mapping(target = "recallStatus", source = "status")
    @Mapping(target = "lastUpdateDateTime", source = "lastUpdateDatetime")
    public abstract Recall1Source toModel(RecallDTO recallDTO);
}
