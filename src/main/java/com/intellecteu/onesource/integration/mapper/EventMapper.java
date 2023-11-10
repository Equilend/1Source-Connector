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
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.dto.PlatformDto;
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
import com.intellecteu.onesource.integration.model.Party;
import com.intellecteu.onesource.integration.model.Platform;
import com.intellecteu.onesource.integration.model.Price;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventMapper {

    private final ObjectMapper objectMapper;

    public TradeEvent toEventEntity(TradeEventDto tradeEventDto) {
        return TradeEvent.builder()
            .eventDatetime(tradeEventDto.getEventDatetime())
            .eventId(tradeEventDto.getEventId())
            .eventType(tradeEventDto.getEventType())
            .resourceUri(tradeEventDto.getResourceUri())
            .processingStatus(ProcessingStatus.NEW)
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
            .lastEventId(contractDto.getLastEventId())
            .lastUpdateDatetime(contractDto.getLastUpdateDatetime())
            .lastUpdatePartyId(contractDto.getLastUpdatePartyId())
            .trade(toTradeAgreementEntity(contractDto.getTrade()))
            .settlementStatus(contractDto.getSettlementStatus())
            .settlement(toSettlementList(contractDto.getSettlement()))
            .processingStatus(contractDto.getProcessingStatus())
            .eventType(contractDto.getEventType())
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
            .lastEventId(contract.getLastEventId())
            .lastUpdateDatetime(contract.getLastUpdateDatetime())
            .lastUpdatePartyId(contract.getLastUpdatePartyId())
            .trade(toTradeAgreementDto(contract.getTrade()))
            .settlementStatus(contract.getSettlementStatus())
            .settlement(toSettlementDtoList(contract.getSettlement()))
            .processingStatus(contract.getProcessingStatus())
            .eventType(contract.getEventType())
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
            .instruction(toInstructionEntity(settlementDto.getInstruction()))
            .build();
    }

    public SettlementDto toSettlementDto(Settlement settlement) {
        if (settlement == null) {
            return null;
        }
        return SettlementDto.builder()
            .partyRole(settlement.getPartyRole())
            .instruction(toInstructionDto(settlement.getInstruction()))
            .build();
    }

    public SettlementInstruction toInstructionEntity(SettlementInstructionDto instructionDto) {
        if (instructionDto == null) {
            return null;
        } else {
            return SettlementInstruction.builder()
                .localAgentAcct(instructionDto.getLocalAgentAcct())
                .localAgentBic(instructionDto.getLocalAgentBic())
                .localAgentName(instructionDto.getLocalAgentName())
                .settlementBic(instructionDto.getSettlementBic())
                .localMarketField(toMarketFields(instructionDto.getLocalMarketFields()))
                .build();
        }
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

    public SettlementInstructionDto toInstructionDto(SettlementInstruction instruction) {
        if (instruction == null) {
            return null;
        }
        return SettlementInstructionDto.builder()
            .localAgentAcct(instruction.getLocalAgentAcct())
            .localAgentBic(instruction.getLocalAgentBic())
            .localAgentName(instruction.getLocalAgentName())
            .settlementBic(instruction.getSettlementBic())
            .localMarketFields(toMarketFieldsDto(instruction.getLocalMarketField()))
            .build();
    }

    public LocalMarketFieldDto toMarketDto(LocalMarketField marketField) {
        return LocalMarketFieldDto.builder()
            .localFieldName(marketField.getLocalFieldName())
            .localFieldValue(marketField.getLocalFieldValue())
            .build();
    }

    public List<LocalMarketFieldDto> toMarketFieldsDto(List<LocalMarketField> marketFields) {
        if (marketFields == null) return null;
        return marketFields.stream().filter(Objects::nonNull).map(this::toMarketDto).collect(Collectors.toList());
    }

    public Agreement toAgreementEntity(AgreementDto agreementDto) {
        if (agreementDto == null) {
            return null;
        }
        return Agreement.builder()
            .id(agreementDto.getId())
            .agreementId(agreementDto.getAgreementId())
            .status(agreementDto.getStatus())
            .trade(toTradeAgreementEntity(agreementDto.getTrade()))
            .eventType(agreementDto.getEventType())
            .flowStatus(agreementDto.getFlowStatus())
            .lastUpdateDatetime(agreementDto.getLastUpdateDatetime())
            .build();
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
            .venueParties(toVenueParties(venueDto.getVenueParties()))
            .platform(toPlatformEntity(venueDto.getPlatform()))
            .type(venueDto.getType())
            .build();
    }

    public Platform toPlatformEntity(PlatformDto platformDto) {
        return Platform.builder()
            .mic(platformDto.getMic())
            .gleifLei(platformDto.getGleifLei())
            .legalName(platformDto.getLegalName())
            .transactionDatetime(platformDto.getTransactionDatetime())
            .venueName(platformDto.getVenueName())
            .venueRefId(platformDto.getVenueRefId())
            .build();
    }

    public List<VenueParty> toVenueParties(List<VenuePartyDto> venuePartyDtoList) {
        return venuePartyDtoList.stream().map(this::toVenueParty).collect(Collectors.toList());
    }

    public VenueParty toVenueParty(VenuePartyDto venuePartyDto) {
        return VenueParty.builder()
            .venueId(venuePartyDto.getVenuePartyId())
            .internalRef(toInternalRefEntity(venuePartyDto.getInternalRef()))
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
            .accountId(internalReferenceDto.getAccountId())
            .build();
    }

    public Instrument toInstrumentEntity(InstrumentDto instrumentDto) {
        return Instrument.builder()
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
        return AgreementDto.builder()
            .id(agreement.getId())
            .agreementId(agreement.getAgreementId())
            .status(agreement.getStatus())
            .trade(toTradeAgreementDto(agreement.getTrade()))
            .eventType(agreement.getEventType())
            .flowStatus(agreement.getFlowStatus())
            .lastUpdateDatetime(agreement.getLastUpdateDatetime())
            .build();
    }

    public List<TransactingPartyDto> toTransactingDtoParties(List<TransactingParty> transactingParty) {
        return transactingParty.stream().map(this::toTransactingPartyDto).collect(Collectors.toList());
    }

    public TransactingPartyDto toTransactingPartyDto(TransactingParty transactingParty) {
        return TransactingPartyDto.builder()
            .partyRole(transactingParty.getPartyRole())
            .party(toPartyDto(transactingParty.getParty()))
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
            .venueParties(toVenueDtoParties(venue.getVenueParties()))
            .platform(toPlatformDto(venue.getPlatform()))
            .type(venue.getType())
            .build();
    }

    public PlatformDto toPlatformDto(Platform platform) {
        return PlatformDto.builder()
            .mic(platform.getMic())
            .gleifLei(platform.getGleifLei())
            .legalName(platform.getLegalName())
            .transactionDatetime(platform.getTransactionDatetime())
            .venueName(platform.getVenueName())
            .venueRefId(platform.getVenueRefId())
            .build();
    }

    public List<VenuePartyDto> toVenueDtoParties(List<VenueParty> venueParty) {
        return venueParty.stream().map(this::toVenuePartyDto).collect(Collectors.toList());
    }

    public VenuePartyDto toVenuePartyDto(VenueParty venueParty) {
        return VenuePartyDto.builder()
            .venuePartyId(venueParty.getVenueId())
            .internalRef(toInternalRefDto(venueParty.getInternalRef()))
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
            .accountId(internalReference.getAccountId())
            .build();
    }

    public InstrumentDto toInstrumentDto(Instrument instrument) {
        return InstrumentDto.builder()
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
