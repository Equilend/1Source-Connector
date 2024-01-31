package com.intellecteu.onesource.integration.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.CollateralDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.FeeRateDto;
import com.intellecteu.onesource.integration.dto.FixedRateDto;
import com.intellecteu.onesource.integration.dto.FloatingRateDto;
import com.intellecteu.onesource.integration.dto.InstrumentDto;
import com.intellecteu.onesource.integration.dto.InternalReferenceDto;
import com.intellecteu.onesource.integration.dto.LocalMarketFieldDto;
import com.intellecteu.onesource.integration.dto.LocalVenueFieldsDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.dto.PriceDto;
import com.intellecteu.onesource.integration.dto.RateDto;
import com.intellecteu.onesource.integration.dto.RebateRateDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.SettlementInstructionDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.TradeEventDto;
import com.intellecteu.onesource.integration.dto.TransactingPartyDto;
import com.intellecteu.onesource.integration.dto.VenueDto;
import com.intellecteu.onesource.integration.dto.VenuePartyDto;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Collateral;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.FeeRate;
import com.intellecteu.onesource.integration.model.FixedRate;
import com.intellecteu.onesource.integration.model.FloatingRate;
import com.intellecteu.onesource.integration.model.Instrument;
import com.intellecteu.onesource.integration.model.InternalReference;
import com.intellecteu.onesource.integration.model.LocalMarketField;
import com.intellecteu.onesource.integration.model.LocalVenueField;
import com.intellecteu.onesource.integration.model.Party;
import com.intellecteu.onesource.integration.model.Price;
import com.intellecteu.onesource.integration.model.Rate;
import com.intellecteu.onesource.integration.model.RebateRate;
import com.intellecteu.onesource.integration.model.Settlement;
import com.intellecteu.onesource.integration.model.SettlementInstruction;
import com.intellecteu.onesource.integration.model.SettlementTemp;
import com.intellecteu.onesource.integration.model.Timestamp;
import com.intellecteu.onesource.integration.model.TradeAgreement;
import com.intellecteu.onesource.integration.model.TradeEvent;
import com.intellecteu.onesource.integration.model.TransactingParty;
import com.intellecteu.onesource.integration.model.Venue;
import com.intellecteu.onesource.integration.model.VenueParty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Getter
@RequiredArgsConstructor
public class EventMapper {

    private final ObjectMapper objectMapper;

    public TradeEvent toEventEntity(TradeEventDto tradeEventDto) {
        return TradeEvent.builder()
            .id(tradeEventDto.getId())
            .eventDatetime(tradeEventDto.getEventDatetime())
            .eventId(tradeEventDto.getEventId())
            .eventType(tradeEventDto.getEventType())
            .resourceUri(tradeEventDto.getResourceUri())
            .processingStatus(tradeEventDto.getProcessingStatus())
            .build();
    }

    public TradeEventDto toEventDto(TradeEvent tradeEvent) {
        return TradeEventDto.builder()
            .id(tradeEvent.getId())
            .eventDatetime(tradeEvent.getEventDatetime())
            .eventId(tradeEvent.getEventId())
            .eventType(tradeEvent.getEventType())
            .resourceUri(tradeEvent.getResourceUri())
            .processingStatus(tradeEvent.getProcessingStatus())
            .build();
    }

    public Contract toContractEntity(ContractDto contractDto) {
        if (contractDto == null) {
            return null;
        }
        return Contract.builder()
            .id(contractDto.getId())
            .contractId(contractDto.getContractId())
            .contractStatus(contractDto.getContractStatus())
            .lastEvent(toEventEntity(contractDto.getLastEvent()))
            .lastUpdateDatetime(contractDto.getLastUpdateDatetime())
            .lastUpdatePartyId(contractDto.getLastUpdatePartyId())
            .trade(toTradeAgreementEntity(contractDto.getTrade()))
            .settlementStatus(contractDto.getSettlementStatus())
            .settlement(toSettlementList(contractDto.getSettlement()))
            .processingStatus(contractDto.getProcessingStatus())
            .eventType(contractDto.getEventType())
            .matchingSpirePositionId(contractDto.getMatchingSpirePositionId())
            .flowStatus(contractDto.getFlowStatus())
            .build();
    }

    public ContractDto toContractDto(Contract contract) {
        if (contract == null) {
            return null;
        }
        return ContractDto.builder()
            .id(contract.getId())
            .contractId(contract.getContractId())
            .contractStatus(contract.getContractStatus())
            .lastEvent(toEventDto(contract.getLastEvent()))
            .lastUpdateDatetime(contract.getLastUpdateDatetime())
            .lastUpdatePartyId(contract.getLastUpdatePartyId())
            .trade(toTradeAgreementDto(contract.getTrade()))
            .settlementStatus(contract.getSettlementStatus())
            .settlement(toSettlementDtoList(contract.getSettlement()))
            .processingStatus(contract.getProcessingStatus())
            .eventType(contract.getEventType())
            .matchingSpirePositionId(contract.getMatchingSpirePositionId())
            .flowStatus(contract.getFlowStatus())
            .build();
    }

    public SettlementTemp toSettlementTempEntity(List<SettlementDto> settlementDtos, String contractId) {
        return SettlementTemp.builder()
            .contractId(contractId)
            .settlement(toSettlementList(settlementDtos))
            .build();
    }

    public List<Settlement> toSettlementList(List<SettlementDto> settlementDtos) {
        return settlementDtos.stream().map(this::toSettlementEntity).collect(Collectors.toList());
    }

    public List<SettlementDto> toSettlementDtoList(List<Settlement> settlementList) {
        return settlementList.stream().map(this::toSettlementDto).toList();
    }

    public Settlement toSettlementEntity(SettlementDto settlementDto) {
        if (settlementDto == null) {
            return null;
        }
        return Settlement.builder()
            .partyRole(settlementDto.getPartyRole())
            .instructionId(settlementDto.getInstructionId())
            .instruction(toInstructionEntity(settlementDto.getInstruction()))
            .build();
    }

    public SettlementDto toSettlementDto(Settlement settlement) {
        if (settlement == null) {
            return null;
        }
        return SettlementDto.builder()
            .partyRole(settlement.getPartyRole())
            .instructionId(settlement.getInstructionId())
            .instruction(toInstructionDto(settlement.getInstruction()))
            .build();
    }

    public SettlementInstruction toInstructionEntity(SettlementInstructionDto instructionDto) {
        if (instructionDto == null) {
            return null;
        }
        return objectMapper.convertValue(instructionDto, SettlementInstruction.class);
    }

    public List<LocalMarketField> toMarketFields(List<LocalMarketFieldDto> marketFieldDtos) {
        return marketFieldDtos.stream().map(this::toMarketEntity).collect(Collectors.toList());
    }

    public LocalMarketField toMarketEntity(LocalMarketFieldDto marketFieldDto) {
        return LocalMarketField.builder()
            .localFieldName(marketFieldDto.getLocalFieldName())
            .localFieldValue(marketFieldDto.getLocalFieldValue())
            .build();
    }

    public Set<LocalVenueField> toVenueFields(List<LocalVenueFieldsDto> venueFieldDtos) {
        return venueFieldDtos.stream().map(this::toVenueEntity).collect(Collectors.toSet());
    }

    public LocalVenueField toVenueEntity(LocalVenueFieldsDto venueFieldsDto) {
        return LocalVenueField.builder()
            .localFieldName(venueFieldsDto.getLocalFieldName())
            .localFieldValue(venueFieldsDto.getLocalFieldValue())
            .build();
    }

    public SettlementInstructionDto toInstructionDto(SettlementInstruction instruction) {
        if (instruction == null) {
            return null;
        }
        return objectMapper.convertValue(instruction, SettlementInstructionDto.class);
    }

    public LocalMarketFieldDto toMarketDto(LocalMarketField marketField) {
        return LocalMarketFieldDto.builder()
            .localFieldName(marketField.getLocalFieldName())
            .localFieldValue(marketField.getLocalFieldValue())
            .build();
    }

    public List<LocalMarketFieldDto> toMarketFieldsDto(List<LocalMarketField> marketFields) {
        if (marketFields == null) {
            return null;
        }
        return marketFields.stream().filter(Objects::nonNull).map(this::toMarketDto).collect(Collectors.toList());
    }

    public LocalVenueFieldsDto toVenueFieldDto(LocalVenueField venueField) {
        return LocalVenueFieldsDto.builder()
            .localFieldName(venueField.getLocalFieldName())
            .localFieldValue(venueField.getLocalFieldValue())
            .build();
    }

    public List<LocalVenueFieldsDto> toVenueFieldsDto(Set<LocalVenueField> venueFields) {
        if (venueFields == null) {
            return null;
        }
        return venueFields.stream().filter(Objects::nonNull).map(this::toVenueFieldDto).collect(Collectors.toList());
    }

    public Agreement toAgreementEntity(AgreementDto agreementDto) {
        if (agreementDto == null) {
            return null;
        }
        return objectMapper.convertValue(agreementDto, Agreement.class);
    }

    public TradeAgreement toTradeAgreementEntity(TradeAgreementDto tradeAgreementDto) {
        return TradeAgreement.builder()
            .id(tradeAgreementDto.getId())
            .venue(toVenueEntity(tradeAgreementDto.getExecutionVenue()))
            .instrument(toInstrumentEntity(tradeAgreementDto.getInstrument()))
            .rate(toRateEntity(tradeAgreementDto.getRate()))
            .quantity(tradeAgreementDto.getQuantity())
            .billingCurrency(tradeAgreementDto.getBillingCurrency())
            .dividendRatePct(tradeAgreementDto.getDividendRatePct())
            .tradeDate(tradeAgreementDto.getTradeDate())
            .termType(tradeAgreementDto.getTermType())
            .termDate(tradeAgreementDto.getTermDate())
            .settlementDate(tradeAgreementDto.getSettlementDate())
            .settlementType(tradeAgreementDto.getSettlementType())
            .collateral(toCollateralEntity(tradeAgreementDto.getCollateral()))
            .transactingParties(toTransactingParties(tradeAgreementDto.getTransactingParties()))
            .eventId(tradeAgreementDto.getEventId())
            .resourceUri(tradeAgreementDto.getResourceUri())
            .processingStatus(tradeAgreementDto.getProcessingStatus())
            .build();
    }

    public TradeAgreementDto toTradeAgreementDto(TradeAgreement tradeAgreement) {
        return TradeAgreementDto.builder()
            .id(tradeAgreement.getId())
            .executionVenue(toVenueDto(tradeAgreement.getVenue()))
            .instrument(toInstrumentDto(tradeAgreement.getInstrument()))
            .rate(toRateDto(tradeAgreement.getRate()))
            .quantity(tradeAgreement.getQuantity())
            .billingCurrency(tradeAgreement.getBillingCurrency())
            .dividendRatePct(tradeAgreement.getDividendRatePct())
            .tradeDate(tradeAgreement.getTradeDate())
            .termType(tradeAgreement.getTermType())
            .termDate(tradeAgreement.getTermDate())
            .settlementDate(tradeAgreement.getSettlementDate())
            .settlementType(tradeAgreement.getSettlementType())
            .collateral(toCollateralDto(tradeAgreement.getCollateral()))
            .transactingParties(toTransactingDtoParties(tradeAgreement.getTransactingParties()))
            .eventId(tradeAgreement.getEventId())
            .resourceUri(tradeAgreement.getResourceUri())
            .processingStatus(tradeAgreement.getProcessingStatus())
            .build();
    }

    public List<TransactingParty> toTransactingParties(List<TransactingPartyDto> transactingPartyDtos) {
        return transactingPartyDtos.stream().map(this::toTransactingPartyEntity).collect(Collectors.toList());
    }

    public TransactingParty toTransactingPartyEntity(TransactingPartyDto transactingPartyDto) {
        return TransactingParty.builder()
            .partyRole(transactingPartyDto.getPartyRole())
            .party(toPartyEntity(transactingPartyDto.getParty()))
            .internalRef(toInternalRefEntity(transactingPartyDto.getInternalRef()))
            .build();
    }

    public Party toPartyEntity(PartyDto partyDto) {
        return Party.builder()
            .partyId(partyDto.getPartyId())
            .internalPartyId(partyDto.getInternalPartyId())
            .partyName(partyDto.getPartyName())
            .gleifLei(partyDto.getGleifLei())
            .build();
    }

    public Collateral toCollateralEntity(CollateralDto collateralDto) {
        return Collateral.builder()
            .collateralValue(collateralDto.getCollateralValue())
            .currency(collateralDto.getCurrency())
            .description(collateralDto.getDescriptionCd())
            .contractPrice(collateralDto.getContractPrice())
            .contractValue(collateralDto.getContractValue())
            .margin(collateralDto.getMargin())
            .roundingRule(collateralDto.getRoundingRule())
            .roundingMode(collateralDto.getRoundingMode())
            .type(collateralDto.getType())
            .build();
    }

    public Venue toVenueEntity(VenueDto venueDto) {
        return Venue.builder()
            .id(venueDto.getId())
            .partyId(venueDto.getPartyId())
            .venueName(venueDto.getVenueName())
            .venueRefKey(venueDto.getVenueRefKey())
            .transactionDatetime(venueDto.getTransactionDatetime())
            .venueParties(toVenueParties(venueDto.getVenueParties()))
            .localVenueFields(toVenueFields(venueDto.getLocalVenueFields()))
            .type(venueDto.getType())
            .build();
    }

    public Set<VenueParty> toVenueParties(List<VenuePartyDto> venuePartyDtoList) {
        return venuePartyDtoList.stream().map(this::toVenueParty).collect(Collectors.toSet());
    }

    public VenueParty toVenueParty(VenuePartyDto venuePartyDto) {
        return VenueParty.builder()
            .venueId(venuePartyDto.getVenuePartyRefKey())
            .partyRole(venuePartyDto.getPartyRole())
            .build();
    }

    public InternalReference toInternalRefEntity(InternalReferenceDto internalReferenceDto) {
        if (internalReferenceDto == null) {
            return null;
        }
        return InternalReference.builder()
            .internalRefId(internalReferenceDto.getInternalRefId())
            .brokerCd(internalReferenceDto.getBrokerCd())
            .accountId(String.valueOf(internalReferenceDto.getAccountId()))
            .build();
    }

    public Instrument toInstrumentEntity(InstrumentDto instrumentDto) {
        return Instrument.builder()
            .id(instrumentDto.getId())
            .cusip(instrumentDto.getCusip())
            .figi(instrumentDto.getFigi())
            .quick(instrumentDto.getQuick())
            .sedol(instrumentDto.getSedol())
            .isin(instrumentDto.getIsin())
            .description(instrumentDto.getDescription())
            .ticker(instrumentDto.getTicker())
            .price(toPriceEntity(instrumentDto.getPrice()))
            .build();
    }

    public Price toPriceEntity(PriceDto priceDto) {
        if (priceDto == null) {
            return null;
        }
        return Price.builder()
            .unit(priceDto.getUnit())
            .currency(priceDto.getCurrency())
            .value(priceDto.getValue())
            .build();
    }

    public Rate toRateEntity(RateDto rateDto) {
        RebateRate rebate = null;
        FeeRate fee = null;
        if (rateDto.getRebate() != null) {
            rebate = toRebateEntity(rateDto.getRebate());
        }
        if (rateDto.getFee() != null) {
            fee = toFeeEntity(rateDto.getFee());
        }
        return Rate.builder()
            .rebate(rebate)
            .fee(fee)
            .build();
    }

    private FeeRate toFeeEntity(FeeRateDto fee) {
        return FeeRate.builder()
            .baseRate(fee.getBaseRate())
            .effectiveRate(fee.getEffectiveRate())
            .effectiveDate(fee.getEffectiveDate())
            .cutoffTime(fee.getCutoffTime())
            .build();
    }

    private RebateRate toRebateEntity(RebateRateDto rebate) {
        FixedRate fixed = null;
        FloatingRate floating = null;
        if (rebate.getFixed() != null) {
            fixed = toFixedEntity(rebate.getFixed());
        }
        if (rebate.getFloating() != null) {
            floating = toFloatingEntity(rebate.getFloating());
        }
        return RebateRate.builder()
            .fixed(fixed)
            .floating(floating)
            .build();
    }

    private FloatingRate toFloatingEntity(FloatingRateDto floating) {
        return FloatingRate.builder()
            .benchmark(floating.getBenchmark())
            .baseRate(floating.getBaseRate())
            .spread(floating.getSpread())
            .effectiveRate(floating.getEffectiveRate())
            .isAutoRerate(floating.getIsAutoRerate())
            .effectiveDateDelay(floating.getEffectiveDateDelay())
            .effectiveDate(floating.getEffectiveDate())
            .cutoffTime(floating.getCutoffTime())
            .build();
    }

    private FixedRate toFixedEntity(FixedRateDto fixed) {
        return FixedRate.builder()
            .baseRate(fixed.getBaseRate())
            .effectiveRate(fixed.getEffectiveRate())
            .effectiveDate(fixed.getEffectiveDate())
            .cutoffTime(fixed.getCutoffTime())
            .build();
    }

    public Timestamp toTimestampEntity(LocalDateTime timestamp) {
        return Timestamp.builder()
            .timestamp(timestamp)
            .build();
    }

    public AgreementDto toAgreementDto(Agreement agreement) {
        if (agreement == null) {
            return null;
        }
        return objectMapper.convertValue(agreement, AgreementDto.class);
    }

    public List<TransactingPartyDto> toTransactingDtoParties(List<TransactingParty> transactingParty) {
        return transactingParty.stream().map(this::toTransactingPartyDto).collect(Collectors.toList());
    }

    public TransactingPartyDto toTransactingPartyDto(TransactingParty transactingParty) {
        return TransactingPartyDto.builder()
            .partyRole(transactingParty.getPartyRole())
            .party(toPartyDto(transactingParty.getParty()))
            .internalRef(toInternalRefDto(transactingParty.getInternalRef()))
            .build();
    }

    public PartyDto toPartyDto(Party party) {
        return PartyDto.builder()
            .partyId(party.getPartyId())
            .internalPartyId(party.getInternalPartyId())
            .partyName(party.getPartyName())
            .gleifLei(party.getGleifLei())
            .build();
    }

    public CollateralDto toCollateralDto(Collateral collateral) {
        return CollateralDto.builder()
            .collateralValue(collateral.getCollateralValue())
            .currency(collateral.getCurrency())
            .descriptionCd(collateral.getDescription())
            .contractPrice(collateral.getContractPrice())
            .contractValue(collateral.getContractValue())
            .margin(collateral.getMargin())
            .roundingRule(collateral.getRoundingRule())
            .roundingMode(collateral.getRoundingMode())
            .type(collateral.getType())
            .build();
    }

    public VenueDto toVenueDto(Venue venue) {
        return VenueDto.builder()
            .id(venue.getId())
            .venueRefKey(venue.getVenueRefKey())
            .venueName(venue.getVenueName())
            .partyId(venue.getPartyId())
            .transactionDatetime(venue.getTransactionDatetime())
            .venueParties(toVenueDtoParties(venue.getVenueParties()))
            .localVenueFields(toVenueFieldsDto(venue.getLocalVenueFields()))
            .type(venue.getType())
            .build();
    }

    public List<VenuePartyDto> toVenueDtoParties(Set<VenueParty> venueParty) {
        return venueParty.stream().map(this::toVenuePartyDto).collect(Collectors.toList());
    }

    public VenuePartyDto toVenuePartyDto(VenueParty venueParty) {
        return VenuePartyDto.builder()
            .venuePartyRefKey(venueParty.getVenueId())
            .partyRole(venueParty.getPartyRole())
            .build();
    }

    public InternalReferenceDto toInternalRefDto(InternalReference internalReference) {
        if (internalReference == null) {
            return null;
        }
        return InternalReferenceDto.builder()
            .internalRefId(internalReference.getInternalRefId())
            .brokerCd(internalReference.getBrokerCd())
            .accountId(Long.valueOf(internalReference.getAccountId()))
            .build();
    }

    public InstrumentDto toInstrumentDto(Instrument instrument) {
        return InstrumentDto.builder()
            .id(instrument.getId())
            .cusip(instrument.getCusip())
            .figi(instrument.getFigi())
            .quick(instrument.getQuick())
            .sedol(instrument.getSedol())
            .isin(instrument.getIsin())
            .description(instrument.getDescription())
            .ticker(instrument.getTicker())
            .price(toPriceDto(instrument.getPrice()))
            .build();
    }

    public PriceDto toPriceDto(Price price) {
        if (price == null) {
            return null;
        }
        return PriceDto.builder()
            .unit(price.getUnit())
            .currency(price.getCurrency())
            .value(price.getValue())
            .build();
    }

    public RateDto toRateDto(Rate rate) {
        RebateRateDto rebate = null;
        FeeRateDto fee = null;
        if (rate.getRebate() != null) {
            rebate = toRebateDto(rate.getRebate());
        }
        if (rate.getFee() != null) {
            fee = toFeeDto(rate.getFee());
        }
        return RateDto.builder()
            .rebate(rebate)
            .fee(fee)
            .build();
    }

    private FeeRateDto toFeeDto(FeeRate fee) {
        return FeeRateDto.builder()
            .baseRate(fee.getBaseRate())
            .effectiveRate(fee.getEffectiveRate())
            .effectiveDate(fee.getEffectiveDate())
            .cutoffTime(fee.getCutoffTime())
            .build();
    }

    private RebateRateDto toRebateDto(RebateRate rebate) {
        FixedRateDto fixed = null;
        FloatingRateDto floating = null;
        if (rebate.getFixed() != null) {
            fixed = toFixedDto(rebate.getFixed());
        }
        if (rebate.getFloating() != null) {
            floating = toFloatingDto(rebate.getFloating());
        }
        return RebateRateDto.builder()
            .fixed(fixed)
            .floating(floating)
            .build();
    }

    private FloatingRateDto toFloatingDto(FloatingRate floating) {
        return FloatingRateDto.builder()
            .benchmark(floating.getBenchmark())
            .baseRate(floating.getBaseRate())
            .spread(floating.getSpread())
            .effectiveRate(floating.getEffectiveRate())
            .isAutoRerate(floating.getIsAutoRerate())
            .effectiveDateDelay(floating.getEffectiveDateDelay())
            .effectiveDate(floating.getEffectiveDate())
            .cutoffTime(floating.getCutoffTime())
            .build();
    }

    private FixedRateDto toFixedDto(FixedRate fixed) {
        return FixedRateDto.builder()
            .baseRate(fixed.getBaseRate())
            .effectiveRate(fixed.getEffectiveRate())
            .effectiveDate(fixed.getEffectiveDate())
            .cutoffTime(fixed.getCutoffTime())
            .build();
    }

    public String getIfExist(JsonNode value, String path) {
        if (value != null) {
            if (value.get(path) != null) {
                return value.get(path).toString().replace("\"", "");
            }
        }
        return null;
    }
}
