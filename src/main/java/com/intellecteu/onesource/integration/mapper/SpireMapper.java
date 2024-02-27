package com.intellecteu.onesource.integration.mapper;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.FIXED_RATE;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.onesource.PriceUnit.LOT;
import static com.intellecteu.onesource.integration.model.onesource.PriceUnit.SHARE;
import static com.intellecteu.onesource.integration.model.onesource.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.DVP;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.FOP;
import static com.intellecteu.onesource.integration.model.onesource.TermType.OPEN;
import static com.intellecteu.onesource.integration.model.onesource.TermType.TERM;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.onesource.Collateral;
import com.intellecteu.onesource.integration.model.onesource.CollateralType;
import com.intellecteu.onesource.integration.model.onesource.CurrencyCd;
import com.intellecteu.onesource.integration.model.onesource.FixedRate;
import com.intellecteu.onesource.integration.model.onesource.FloatingRate;
import com.intellecteu.onesource.integration.model.onesource.Instrument;
import com.intellecteu.onesource.integration.model.onesource.InternalReference;
import com.intellecteu.onesource.integration.model.onesource.Party;
import com.intellecteu.onesource.integration.model.onesource.Price;
import com.intellecteu.onesource.integration.model.onesource.PriceUnit;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.RebateRate;
import com.intellecteu.onesource.integration.model.onesource.SettlementType;
import com.intellecteu.onesource.integration.model.onesource.TermType;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.model.onesource.TransactingParty;
import com.intellecteu.onesource.integration.model.onesource.Venue;
import com.intellecteu.onesource.integration.model.onesource.VenueParty;
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.TradeOutDTO;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Deprecated(since = "0.0.5-SNAPSHOT")
public class SpireMapper {

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

    public Position toPosition(TradeOutDTO tradeOutDto) {
        if (tradeOutDto == null) {
            return null;
        }
        return objectMapper.convertValue(tradeOutDto, Position.class);
    }

    public Position toPosition(PositionOutDTO positionOutDTO) {
        if (positionOutDTO == null) {
            return null;
        }
        return objectMapper.convertValue(positionOutDTO, Position.class);
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

    public TradeAgreement buildTradeAgreement(Position position) {
        return TradeAgreement.builder()
            .venue(buildVenue(position))
            .instrument(buildInstrument(position))
            .rate(buildRate(position))
            .quantity(position.getQuantity().intValue())
            .billingCurrency(CurrencyCd.valueOf(position.getCurrency().getCurrencyKy()))
            .dividendRatePct(position.getLoanBorrow().getTaxWithholdingRate().intValue())
            .tradeDate(position.getTradeDate().toLocalDate())
            .termType(checkTermType(position.getTermId()))
            .termDate(position.getEndDate().toLocalDate())
            .settlementDate(position.getSettleDate().toLocalDate())
            .settlementType(checkSettlementType(position.getDeliverFree()))
            .collateral(buildCollateral(position))
            .transactingParties(
                List.of(createLenderTransactionParty(position), createBorrowerTransactionParty(position)))
            .build();
    }

    private TransactingParty createLenderTransactionParty(Position position) {
        return TransactingParty.builder()
            .partyRole(LENDER)
            .party(createLenderParty(position))
            .internalRef(createInternalRef(position))
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

    private TransactingParty createBorrowerTransactionParty(Position position) {
        return TransactingParty.builder()
            .partyRole(BORROWER)
            .party(createBorrowerParty(position))
            .build();
    }

    private static Party createLenderParty(Position position) {
        return Party.builder()
            .partyName(position.getShortName())
            .gleifLei(position.getAccountLei())
            .build();
    }

    private static Party createBorrowerParty(Position position) {
        return Party.builder()
            .gleifLei(position.getCpLei())
            .build();
    }

    private static InternalReference createInternalRef(Position position) {
        return InternalReference.builder()
            .accountId(String.valueOf(position.getPositionAccount().getAccountId()))
            .internalRefId(String.valueOf(position.getPositionId()))
            .build();
    }

    public Collateral buildCollateral(Position position) {
        return Collateral.builder()
            .contractPrice(position.getPrice())
            .contractValue(position.getContractValue())
            .collateralValue(position.getAmount())
            .currency(position.getCurrency().getCurrencyKy())
            .type(CollateralType.valueOf(position.getCollateralType()))
            .margin(position.getCpHaircut())
            .roundingRule(position.getCpMarkRoundTo())
            .roundingMode(ALWAYSUP)
            .build();
    }

    private Rate buildRate(Position position) {
        return Rate.builder()
            .rebate(buildRebateRate(position))
            .build();
    }

    private RebateRate buildRebateRate(Position position) {
        FloatingRate floatingRate = null;
        FixedRate fixedRate = null;
        String indexName = position.getIndex() == null ? null : position.getIndex().getIndexName();
        if (FIXED_RATE.equals(indexName)) {
            fixedRate = buildFixedRate(position);
        } else {
            floatingRate = buildFloatingRate(position);
        }
        return RebateRate.builder()
            .floating(floatingRate)
            .fixed(fixedRate)
            .build();
    }

    private FloatingRate buildFloatingRate(Position position) {
        if (position == null) {
            return null;
        }
        var spread = position.getIndex() == null ? null : position.getIndex().getSpread();
        return FloatingRate.builder()
            .effectiveRate(position.getRate())
            .baseRate(null) // todo: find how to retrieve baseRate
            .spread(spread)
            .build();
    }

    private FixedRate buildFixedRate(Position position) {
        return FixedRate.builder()
            .baseRate(position.getRate())
            .effectiveRate(position.getRate())
            .effectiveDate(position.getSettleDate().toLocalDate())
            .build();
    }

    public Instrument buildInstrument(Position position) {
        return Instrument.builder()
            .securityId(position.getPositionSecurityId())
            .ticker(position.getPositionSecurityDetail().getTicker())
            .cusip(position.getPositionSecurityDetail().getCusip())
            .isin(position.getPositionSecurityDetail().getIsin())
            .sedol(position.getPositionSecurityDetail().getSedol())
            .quickCode(position.getPositionSecurityDetail().getQuickCode())
            .description(position.getPositionSecurityDetail().getDescription())
            .price(buildPrice(position))
            .priceFactor(position.getPositionSecurityDetail().getPriceFactor())
            .build();
    }

    private Venue buildVenue(Position position) {
        return Venue.builder()
            .venueRefKey(position.getCustomValue2())
            .venueParties(Set.of(buildLenderVenueParty(position), buildBorrowerVenueParty()))
            .build();

    }

    private Price buildPrice(Position position) {
        return Price.builder()
            .unit(buildPriceUnit(position))
            .value(position.getPrice())
            .currency(position.getCurrency().getCurrencyKy())
            .build();

    }

    private PriceUnit buildPriceUnit(Position position) {
        return position.getPositionSecurityDetail().getPriceFactor() == 1 ? SHARE : LOT;
    }

    public VenueParty buildLenderVenueParty(Position position) {
        return VenueParty.builder()
            .partyRole(LENDER)
            .venueId(String.valueOf(position.getPositionId()))
            .build();
    }

    public VenueParty buildBorrowerVenueParty() {
        return VenueParty.builder()
            .partyRole(BORROWER)
            .build();
    }

    public List<SettlementDto> toSettlementList(ResponseEntity<JsonNode> response) {
        return null;
    }
}
