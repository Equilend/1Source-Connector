package com.intellecteu.onesource.integration.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.CollateralDto;
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
import com.intellecteu.onesource.integration.dto.TransactingPartyDto;
import com.intellecteu.onesource.integration.dto.VenueDto;
import com.intellecteu.onesource.integration.dto.VenuePartyDto;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Collateral;
import com.intellecteu.onesource.integration.model.onesource.FeeRate;
import com.intellecteu.onesource.integration.model.onesource.FixedRate;
import com.intellecteu.onesource.integration.model.onesource.FloatingRate;
import com.intellecteu.onesource.integration.model.onesource.Instrument;
import com.intellecteu.onesource.integration.model.onesource.InternalReference;
import com.intellecteu.onesource.integration.model.onesource.LocalMarketField;
import com.intellecteu.onesource.integration.model.onesource.LocalVenueField;
import com.intellecteu.onesource.integration.model.onesource.Party;
import com.intellecteu.onesource.integration.model.onesource.Price;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.RebateRate;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.SettlementInstruction;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.model.onesource.TransactingParty;
import com.intellecteu.onesource.integration.model.onesource.Venue;
import com.intellecteu.onesource.integration.model.onesource.VenueParty;
import com.intellecteu.onesource.integration.repository.entity.onesource.AgreementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.CollateralEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.FeeRateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.FixedRateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.FloatingRateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.InstrumentEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.InternalReferenceEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.LocalVenueFieldEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.PartyEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.PriceEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.RateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.RebateRateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementInstructionEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeAgreementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.TransactingPartyEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.VenueEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.VenuePartyEntity;
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
@Deprecated(since = "0.0.5-SNAPSHOT", forRemoval = true)
public class EventMapper {

    private final ObjectMapper objectMapper;

    public List<Settlement> toSettlementList(List<SettlementDto> settlementDtos) {
        return settlementDtos.stream().map(this::toSettlementEntity).collect(Collectors.toList());
    }

    public List<SettlementDto> toSettlementDtoList(List<Settlement> settlementList) {
        return settlementList.stream().map(this::toSettlementDto).toList();
    }

    public List<SettlementDto> entitiesToSettlementDtoList(List<SettlementEntity> settlementList) {
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

    public SettlementDto toSettlementDto(SettlementEntity settlement) {
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

    public LocalMarketField toMarketEntity(LocalMarketFieldDto marketFieldDto) {
        return LocalMarketField.builder()
            .localFieldName(marketFieldDto.getLocalFieldName())
            .localFieldValue(marketFieldDto.getLocalFieldValue())
            .build();
    }

    public Set<LocalVenueFieldEntity> toVenueFields(List<LocalVenueFieldsDto> venueFieldDtos) {
        return venueFieldDtos.stream().map(this::toVenueEntity).collect(Collectors.toSet());
    }

    public LocalVenueFieldEntity toVenueEntity(LocalVenueFieldsDto venueFieldsDto) {
        return LocalVenueFieldEntity.builder()
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

    public SettlementInstructionDto toInstructionDto(SettlementInstructionEntity instruction) {
        if (instruction == null) {
            return null;
        }
        return objectMapper.convertValue(instruction, SettlementInstructionDto.class);
    }

    public SettlementInstruction toInstruction(SettlementInstructionEntity instruction) {
        if (instruction == null) {
            return null;
        }
        return objectMapper.convertValue(instruction, SettlementInstruction.class);
    }

    public LocalMarketFieldDto toMarketDto(LocalMarketField marketField) {
        return LocalMarketFieldDto.builder()
            .localFieldName(marketField.getLocalFieldName())
            .localFieldValue(marketField.getLocalFieldValue())
            .build();
    }

    public LocalVenueFieldsDto toVenueFieldDto(LocalVenueField venueField) {
        return LocalVenueFieldsDto.builder()
            .localFieldName(venueField.getLocalFieldName())
            .localFieldValue(venueField.getLocalFieldValue())
            .build();
    }

    public LocalVenueFieldsDto toVenueFieldDto(LocalVenueFieldEntity venueField) {
        return LocalVenueFieldsDto.builder()
            .localFieldName(venueField.getLocalFieldName())
            .localFieldValue(venueField.getLocalFieldValue())
            .build();
    }

    public List<LocalVenueFieldsDto> toVenueFieldsDtoFromModel(Set<LocalVenueField> venueFields) {
        if (venueFields == null) {
            return null;
        }
        return venueFields.stream().filter(Objects::nonNull).map(this::toVenueFieldDto).collect(Collectors.toList());
    }

    public List<LocalVenueFieldsDto> entitiesToVenueFieldsDto(
        Set<com.intellecteu.onesource.integration.repository.entity.onesource.LocalVenueFieldEntity> venueFields) {
        if (venueFields == null) {
            return null;
        }
        return venueFields.stream().filter(Objects::nonNull).map(this::toVenueFieldDto).collect(Collectors.toList());
    }

    public AgreementEntity toAgreementEntity(AgreementDto agreementDto) {
        if (agreementDto == null) {
            return null;
        }
        return objectMapper.convertValue(agreementDto, AgreementEntity.class);
    }

    public Agreement toAgreement(AgreementDto agreementDto) {
        if (agreementDto == null) {
            return null;
        }
        return objectMapper.convertValue(agreementDto, Agreement.class);
    }

    public Agreement toAgreement(AgreementEntity agreemententity) {
        if (agreemententity == null) {
            return null;
        }
        return objectMapper.convertValue(agreemententity, Agreement.class);
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

    public TradeAgreementDto toTradeAgreementDto(TradeAgreementEntity tradeAgreement) {
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
            .transactingParties(entitiesToTransactingDtoParties(tradeAgreement.getTransactingParties()))
            .eventId(tradeAgreement.getEventId())
            .resourceUri(tradeAgreement.getResourceUri())
            .processingStatus(tradeAgreement.getProcessingStatus())
            .build();
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

    public Set<VenuePartyEntity> toVenuePartiesEntity(List<VenuePartyDto> venuePartyDtoList) {
        return venuePartyDtoList.stream().map(this::toVenuePartyEntity).collect(Collectors.toSet());
    }

    public VenuePartyEntity toVenuePartyEntity(VenuePartyDto venuePartyDto) {
        return VenuePartyEntity.builder()
            .venueId(venuePartyDto.getVenuePartyRefKey())
            .partyRole(venuePartyDto.getPartyRole())
            .build();
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

    public InstrumentEntity toInstrumentEntity(InstrumentDto instrumentDto) {
        return InstrumentEntity.builder()
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

    public PriceEntity toPriceEntity(PriceDto priceDto) {
        if (priceDto == null) {
            return null;
        }
        return PriceEntity.builder()
            .unit(priceDto.getUnit())
            .currency(priceDto.getCurrency())
            .value(priceDto.getValue())
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

    public List<TransactingPartyDto> entitiesToTransactingDtoParties(List<TransactingPartyEntity> transactingParty) {
        return transactingParty.stream().map(this::toTransactingPartyDto).collect(Collectors.toList());
    }


    public TransactingPartyDto toTransactingPartyDto(TransactingParty transactingParty) {
        return TransactingPartyDto.builder()
            .partyRole(transactingParty.getPartyRole())
            .party(toPartyDto(transactingParty.getParty()))
            .internalRef(toInternalRefDto(transactingParty.getInternalRef()))
            .build();
    }

    public TransactingPartyDto toTransactingPartyDto(TransactingPartyEntity transactingParty) {
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

    public PartyDto toPartyDto(PartyEntity party) {
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

    public CollateralDto toCollateralDto(CollateralEntity collateral) {
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
            .transactionDatetime(venue.getTransactionDateTime())
            .venueParties(toVenueDtoParties(venue.getVenueParties()))
            .localVenueFields(toVenueFieldsDtoFromModel(venue.getLocalVenueFields()))
            .type(venue.getType())
            .build();
    }

    public VenueDto toVenueDto(VenueEntity venue) {
        return VenueDto.builder()
            .id(venue.getId())
            .venueRefKey(venue.getVenueRefKey())
            .venueName(venue.getVenueName())
            .partyId(venue.getPartyId())
            .transactionDatetime(venue.getTransactionDatetime())
            .venueParties(entityToVenueDtoParties(venue.getVenueParties()))
            .localVenueFields(entitiesToVenueFieldsDto(venue.getLocalVenueFields()))
            .type(venue.getType())
            .build();
    }

    public List<VenuePartyDto> toVenueDtoParties(Set<VenueParty> venueParty) {
        return venueParty.stream().map(this::toVenuePartyDto).collect(Collectors.toList());
    }

    public List<VenuePartyDto> entityToVenueDtoParties(Set<VenuePartyEntity> venueParty) {
        return venueParty.stream().map(this::toVenuePartyDto).collect(Collectors.toList());
    }


    public VenuePartyDto toVenuePartyDto(VenueParty venueParty) {
        return VenuePartyDto.builder()
            .venuePartyRefKey(venueParty.getVenueId())
            .partyRole(venueParty.getPartyRole())
            .build();
    }

    public VenuePartyDto toVenuePartyDto(VenuePartyEntity venueParty) {
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

    public InternalReferenceDto toInternalRefDto(InternalReferenceEntity internalReference) {
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
            .quick(instrument.getQuickCode())
            .sedol(instrument.getSedol())
            .isin(instrument.getIsin())
            .description(instrument.getDescription())
            .ticker(instrument.getTicker())
            .price(toPriceDto(instrument.getPrice()))
            .build();
    }

    public InstrumentDto toInstrumentDto(InstrumentEntity instrument) {
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

    public PriceDto toPriceDto(PriceEntity price) {
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

    public RateDto toRateDto(RateEntity rate) {
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

    private FeeRateDto toFeeDto(FeeRateEntity fee) {
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

    private RebateRateDto toRebateDto(RebateRateEntity rebate) {
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

    private FloatingRateDto toFloatingDto(FloatingRateEntity floating) {
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

    private FixedRateDto toFixedDto(FixedRateEntity fixed) {
        return FixedRateDto.builder()
            .baseRate(fixed.getBaseRate())
            .effectiveRate(fixed.getEffectiveRate())
            .effectiveDate(fixed.getEffectiveDate())
            .cutoffTime(fixed.getCutoffTime())
            .build();
    }

}
