package com.intellecteu.onesource.integration.mapper;

import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.dto.CollateralDto;
import com.intellecteu.onesource.integration.dto.FixedRateDto;
import com.intellecteu.onesource.integration.dto.FloatingRateDto;
import com.intellecteu.onesource.integration.dto.InstrumentDto;
import com.intellecteu.onesource.integration.dto.InternalReferenceDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.dto.PlatformDto;
import com.intellecteu.onesource.integration.dto.RateDto;
import com.intellecteu.onesource.integration.dto.RebateRateDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.TransactingPartyDto;
import com.intellecteu.onesource.integration.dto.VenueDto;
import com.intellecteu.onesource.integration.dto.VenuePartyDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.model.CollateralType;
import com.intellecteu.onesource.integration.model.CurrencyCd;
import com.intellecteu.onesource.integration.model.spire.Position;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PositionMapper {

  private final ObjectMapper objectMapper;

  public PositionDto toPositionDto(Position position) {
    if (position == null) return null;
    return objectMapper.convertValue(position, PositionDto.class);
  }

  public Position toPosition(JsonNode json) throws JsonProcessingException {
    if (json == null) return null;
    return objectMapper.readValue(json.toString(), Position.class);
  }

  public TradeAgreementDto buildTradeAgreementDto(PositionDto positionDto) {
    return TradeAgreementDto.builder()
        .executionVenue(buildVenueDto(positionDto))
        .instrument(buildInstrumentDto(positionDto))
        .rate(buildRateDto(positionDto))
        .quantity(positionDto.getQuantity().intValue())
        .billingCurrency(CurrencyCd.valueOf(positionDto.getCurrency().getCurrencyKy()))
        .dividendRatePct(positionDto.getLoanBorrowDto().getTaxWithholdingRate().intValue())
        .tradeDate(positionDto.getTradeDate().toLocalDate())
        .settlementDate(positionDto.getSettleDate().toLocalDate())
//        .settlementType(positionDto.getDeliverFree())
        .collateral(buildCollateralDto(positionDto))
        .transactingParties(List.of(createLenderTransactionParty(positionDto), createBorrowerTransactionParty(positionDto)))
        .build();
  }

  private TransactingPartyDto createLenderTransactionParty(PositionDto positionDto) {
    return TransactingPartyDto.builder()
        .partyRole(LENDER)
        .party(createLenderPartyDto(positionDto))
        .build();
  }

  private TransactingPartyDto createBorrowerTransactionParty(PositionDto positionDto) {
    return TransactingPartyDto.builder()
        .partyRole(LENDER)
        .party(createBorrowerPartyDto(positionDto))
        .build();
  }

  private static PartyDto createLenderPartyDto(PositionDto positionDto) {
    return PartyDto.builder()
        .gleifLei(positionDto.getAccountLei())
        .build();
  }

  private static PartyDto createBorrowerPartyDto(PositionDto positionDto) {
    return PartyDto.builder()
        .gleifLei(positionDto.getCpLei())
        .build();
  }

  public CollateralDto buildCollateralDto(PositionDto positionDto) {
    return CollateralDto.builder()
        .contractPrice(positionDto.getPrice())
        .contractValue(positionDto.getContractValue())
        .collateralValue(positionDto.getAmount())
        .currency(positionDto.getCurrency().getCurrencyKy())
        .type(CollateralType.valueOf(positionDto.getCollateralType()))
        .margin(positionDto.getCpHaircut())
        .build();
  }

  private RateDto buildRateDto(PositionDto positionDto) {
    return RateDto.builder()
        .rebate(buildRebateRateDto(positionDto))
        .build();
  }

  private RebateRateDto buildRebateRateDto(PositionDto positionDto) {
    return RebateRateDto.builder()
        .floating(buildFloatingRateDto(positionDto))
        .fixed(buildFixedRateDto(positionDto))
        .build();
  }

  private FloatingRateDto buildFloatingRateDto(PositionDto positionDto) {
    return FloatingRateDto.builder()
        .effectiveRate(positionDto.getRate())
        .build();
  }

  private FixedRateDto buildFixedRateDto(PositionDto positionDto) {
    return FixedRateDto.builder()
        .effectiveRate(positionDto.getRate())
        .build();
  }

  public InstrumentDto buildInstrumentDto(PositionDto positionDto) {
    return InstrumentDto.builder()
        .ticker(positionDto.getSecurityDetailDto().getTicker())
        .cusip(positionDto.getSecurityDetailDto().getCusip())
        .isin(positionDto.getSecurityDetailDto().getIsin())
        .sedol(positionDto.getSecurityDetailDto().getSedol())
        .quick(positionDto.getSecurityDetailDto().getQuickCode())
        .figi(positionDto.getSecurityDetailDto().getBloombergId())
        .build();
  }

  private VenueDto buildVenueDto(PositionDto positionDto) {
    return VenueDto.builder()
        .platform(buildPlatformDto(positionDto))
        .build();

  }

  public VenuePartyDto buildVenuePartyDto() {
    return VenuePartyDto.builder()
        .partyRole(LENDER)
        .venuePartyId("testVenuePartyId")
        .internalRef(buildInternalReferenceDto())
        .build();
  }

  private InternalReferenceDto buildInternalReferenceDto() {
    return InternalReferenceDto.builder()
        .brokerCd("testBrokerCd")
        .accountId("testAccountId")
        .internalRefId("testInternalRefId")
        .build();
  }

  private PlatformDto buildPlatformDto(PositionDto positionDto) {
    return PlatformDto.builder()
        .venueRefId(positionDto.getCustomValue2())
        .build();
  }

}
