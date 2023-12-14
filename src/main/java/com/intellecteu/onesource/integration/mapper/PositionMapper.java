package com.intellecteu.onesource.integration.mapper;

import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.model.SettlementType.DVP;
import static com.intellecteu.onesource.integration.model.SettlementType.FOP;
import static com.intellecteu.onesource.integration.model.TermType.OPEN;
import static com.intellecteu.onesource.integration.model.TermType.TERM;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.dto.CollateralDto;
import com.intellecteu.onesource.integration.dto.FixedRateDto;
import com.intellecteu.onesource.integration.dto.FloatingRateDto;
import com.intellecteu.onesource.integration.dto.InstrumentDto;
import com.intellecteu.onesource.integration.dto.InternalReferenceDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.dto.PriceDto;
import com.intellecteu.onesource.integration.dto.RateDto;
import com.intellecteu.onesource.integration.dto.RebateRateDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.TransactingPartyDto;
import com.intellecteu.onesource.integration.dto.VenueDto;
import com.intellecteu.onesource.integration.dto.VenuePartyDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.model.CollateralType;
import com.intellecteu.onesource.integration.model.CurrencyCd;
import com.intellecteu.onesource.integration.model.SettlementType;
import com.intellecteu.onesource.integration.model.TermType;
import com.intellecteu.onesource.integration.model.spire.Position;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PositionMapper {

    private final ObjectMapper objectMapper;

    public PositionDto toPositionDto(Position position) {
        if (position == null) {
            return null;
        }
        return objectMapper.convertValue(position, PositionDto.class);
    }

    public Position toPosition(PositionDto positionDto) {
        if (positionDto == null) {
            return null;
        }
        return objectMapper.convertValue(positionDto, Position.class);
    }

    public Position toPosition(JsonNode json) throws JsonProcessingException {
        if (json == null) {
            return null;
        }
        return objectMapper.readValue(json.toString(), Position.class);
    }

    public PositionDto jsonToPositionDto(JsonNode json) throws JsonProcessingException {
        if (json == null) {
            return null;
        }
        return objectMapper.readValue(json.toString(), PositionDto.class);
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
            .termType(checkTermType(positionDto.getTermId()))
            .termDate(positionDto.getEndDate().toLocalDate())
            .settlementDate(positionDto.getSettleDate().toLocalDate())
            .settlementType(checkSettlementType(positionDto.getDeliverFree()))
            .collateral(buildCollateralDto(positionDto))
            .transactingParties(
                List.of(createLenderTransactionParty(positionDto), createBorrowerTransactionParty(positionDto)))
            .build();
    }

    private TransactingPartyDto createLenderTransactionParty(PositionDto positionDto) {
        return TransactingPartyDto.builder()
            .partyRole(LENDER)
            .party(createLenderPartyDto(positionDto))
            .internalRef(createInternalRefDto(positionDto))
            .build();
    }

    private SettlementType checkSettlementType(Boolean deliverFree) {
        return deliverFree == Boolean.FALSE ? DVP : FOP;
    }

    private TermType checkTermType(Integer termId) {
        if (termId == 1) {
            return OPEN;
        } else if (termId == 2) {
            return TERM;
        }

        return null;
    }

    private TransactingPartyDto createBorrowerTransactionParty(PositionDto positionDto) {
        return TransactingPartyDto.builder()
            .partyRole(BORROWER)
            .party(createBorrowerPartyDto(positionDto))
            .build();
    }

    private static PartyDto createLenderPartyDto(PositionDto positionDto) {
        return PartyDto.builder()
            .partyName(positionDto.getShortName())
            .gleifLei(positionDto.getAccountLei())
            .build();
    }

    private static PartyDto createBorrowerPartyDto(PositionDto positionDto) {
        return PartyDto.builder()
            .gleifLei(positionDto.getCpLei())
            .build();
    }

    private static InternalReferenceDto createInternalRefDto(PositionDto positionDto) {
        return InternalReferenceDto.builder()
            .accountId(positionDto.getAccountDto().getInfo())
            .internalRefId(positionDto.getPositionId())
            .build();
    }

    public CollateralDto buildCollateralDto(PositionDto positionDto) {
        return CollateralDto.builder()
            .contractPrice(positionDto.getPrice())
            .contractValue(positionDto.getContractValue())
            .collateralValue(positionDto.getAmount())
            .currency(positionDto.getCurrency().getCurrencyKy())
            .type(CollateralType.valueOf(positionDto.getCollateralType()))
            //TODO clarification needed
            .margin(positionDto.getCpHaircut())
            .roundingRule(positionDto.getCpMarkRoundTo())
            .roundingMode(ALWAYSUP)
            .build();
    }

    private RateDto buildRateDto(PositionDto positionDto) {
        return RateDto.builder()
            .rebate(buildRebateRateDto(positionDto))
            .build();
    }

    private RebateRateDto buildRebateRateDto(PositionDto positionDto) {
        FloatingRateDto floatingRateDto = null;
        FixedRateDto fixedRateDto = null;
        String indexName = positionDto.getIndexDto().getIndexName();
        if (indexName == "Fixed Rate") {
            fixedRateDto = buildFixedRateDto(positionDto);
        } else {
            floatingRateDto = buildFloatingRateDto(positionDto);
        }
        return RebateRateDto.builder()
            .floating(floatingRateDto)
            .fixed(fixedRateDto)
            .build();
    }

    private FloatingRateDto buildFloatingRateDto(PositionDto positionDto) {
        return FloatingRateDto.builder()
            //TODO benchmark clarification
            .effectiveRate(positionDto.getRate())
            .baseRate(positionDto.getSecurityDetailDto().getBaseRebateRate())
            .spread(positionDto.getIndexDto().getSpread())
            .build();
    }

    private FixedRateDto buildFixedRateDto(PositionDto positionDto) {
        return FixedRateDto.builder()
            .baseRate(positionDto.getSecurityDetailDto().getBaseRebateRate())
            .effectiveRate(positionDto.getRate())
            .effectiveDate(positionDto.getSettleDate().toLocalDate())
            .build();
    }

    public InstrumentDto buildInstrumentDto(PositionDto positionDto) {
        return InstrumentDto.builder()
            .ticker(positionDto.getSecurityDetailDto().getTicker())
            .cusip(positionDto.getSecurityDetailDto().getCusip())
            .isin(positionDto.getSecurityDetailDto().getIsin())
            .sedol(positionDto.getSecurityDetailDto().getSedol())
            .quick(positionDto.getSecurityDetailDto().getQuickCode())
            .description(positionDto.getSecurityDetailDto().getDescription())
            .price(buildPriceDto(positionDto))
            .build();
    }

    private VenueDto buildVenueDto(PositionDto positionDto) {
        return VenueDto.builder()
            .venueRefKey(positionDto.getCustomValue2())
            .venueParties(List.of(buildLenderVenuePartyDto(positionDto), buildBorrowerVenuePartyDto()))
            .build();

    }

    private PriceDto buildPriceDto(PositionDto positionDto) {
        return PriceDto.builder()
            //TODO unit setting clarification
            .value(positionDto.getPrice())
            .currency(positionDto.getCurrency().getCurrencyKy())
            .build();

    }

    public VenuePartyDto buildLenderVenuePartyDto(PositionDto positionDto) {
        return VenuePartyDto.builder()
            .partyRole(LENDER)
            .venuePartyRefKey(positionDto.getPositionId())
            .build();
    }

    public VenuePartyDto buildBorrowerVenuePartyDto() {
        return VenuePartyDto.builder()
            .partyRole(BORROWER)
            .build();
    }

}
