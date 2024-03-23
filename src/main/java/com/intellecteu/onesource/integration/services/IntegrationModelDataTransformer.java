package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.model.onesource.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.onesource.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.DVP;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.FOP;
import static com.intellecteu.onesource.integration.model.onesource.TermType.OPEN;
import static com.intellecteu.onesource.integration.model.onesource.TermType.TERM;

import com.intellecteu.onesource.integration.model.backoffice.Currency;
import com.intellecteu.onesource.integration.model.backoffice.Index;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.PositionAccount;
import com.intellecteu.onesource.integration.model.backoffice.PositionConfirmationRequest;
import com.intellecteu.onesource.integration.model.backoffice.PositionInstruction;
import com.intellecteu.onesource.integration.model.backoffice.PositionSecurityDetail;
import com.intellecteu.onesource.integration.model.onesource.Benchmark;
import com.intellecteu.onesource.integration.model.onesource.Collateral;
import com.intellecteu.onesource.integration.model.onesource.CollateralType;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.ContractProposalApproval;
import com.intellecteu.onesource.integration.model.onesource.CurrencyCd;
import com.intellecteu.onesource.integration.model.onesource.FixedRate;
import com.intellecteu.onesource.integration.model.onesource.FloatingRate;
import com.intellecteu.onesource.integration.model.onesource.Instrument;
import com.intellecteu.onesource.integration.model.onesource.InternalReference;
import com.intellecteu.onesource.integration.model.onesource.LocalVenueField;
import com.intellecteu.onesource.integration.model.onesource.Party;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.Price;
import com.intellecteu.onesource.integration.model.onesource.PriceUnit;
import com.intellecteu.onesource.integration.model.onesource.Rate;
import com.intellecteu.onesource.integration.model.onesource.RebateRate;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.SettlementInstruction;
import com.intellecteu.onesource.integration.model.onesource.SettlementInstructionUpdate;
import com.intellecteu.onesource.integration.model.onesource.SettlementStatus;
import com.intellecteu.onesource.integration.model.onesource.SettlementType;
import com.intellecteu.onesource.integration.model.onesource.TermType;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.model.onesource.TransactingParty;
import com.intellecteu.onesource.integration.model.onesource.Venue;
import com.intellecteu.onesource.integration.model.onesource.VenueParty;
import com.intellecteu.onesource.integration.model.onesource.VenueType;
import io.micrometer.common.util.StringUtils;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IntegrationModelDataTransformer implements IntegrationDataTransformer {

    @Override
    public ContractProposal toLenderContractProposal(Position position) {
        return ContractProposal.builder()
            .trade(buildTradeFromPosition(position))
            .settlementList(List.of(createLenderSettlement(position)))
            .build();
    }

    @Override
    public ContractProposalApproval toBorrowerContractProposalApproval(Contract contract, Position position) {
        return ContractProposalApproval.builder()
            .internalRefId(String.valueOf(position.getPositionId()))
            .settlement(buildBorrowSettlementFromPosition(position))
            .build();
    }

    @Override
    public PositionConfirmationRequest toPositionConfirmationRequest(Position position) {
        return PositionConfirmationRequest.builder()
            .userName("1Source")
            .positionId(position.getPositionId())
            .tradeId(position.getTradeId())
            .ledgerId(position.getMatching1SourceLoanContractId())
            .instructions(buildPositionInstructions(position))
            .build();
    }

    private PositionInstruction buildPositionInstructions(Position position) {
        return PositionInstruction.builder()
            .account(buildPositionAccount(position.getPositionAccount()))
            .build();
    }

    private PositionAccount buildPositionAccount(PositionAccount positionAccount) {
        if (positionAccount == null) {
            return null;
        }
        return PositionAccount.builder()
            .dtc(positionAccount.getDtc())
            .build();
    }

    private SettlementInstructionUpdate buildBorrowSettlementFromPosition(Position position) {
        return SettlementInstructionUpdate.builder()
            .partyRole(BORROWER)
            .instruction(buildBorrowerInstructionFromPosition(position))
            .build();
    }

    private SettlementInstruction buildBorrowerInstructionFromPosition(Position position) {
        return SettlementInstruction.builder()
            .dtcParticipantNumber(String.valueOf(position.getPositionAccount().getDtc()))
            .build();
    }

    private TradeAgreement buildTradeFromPosition(Position position) {
        return TradeAgreement.builder()
            .venue(buildExecutionVenueFromPosition(position))
            .instrument(buildInstrumentFromPosition(position))
            .rate(buildRebateRateFromPosition(position))
            .quantity(position.getQuantity().intValue()) // todo rework to BigDecimal
            .billingCurrency(CurrencyCd.fromValue(position.getCurrency().getCurrencyKy()))
            .dividendRatePct(position.getLoanBorrow().getTaxWithholdingRate())
            .tradeDate(position.getTradeDate().toLocalDate())
            .termType(buildTermType(position.getTermId()))
            .termDate(position.getEndDate().toLocalDate()) // todo ask how to handle NPE
            .settlementDate(position.getSettleDate().toLocalDate())
            .settlementType(buildSettlementType(position.getDeliverFree()))
            .collateral(buildCollateral(position))
            .transactingParties(List.of(
                buildLenderTransactionParty(position),
                buildBorrowerTransactionParty(position)))
            .build();
    }

    private Settlement createLenderSettlement(Position position) {
        return Settlement.builder()
            .partyRole(LENDER)
            .settlementStatus(SettlementStatus.NONE)
            .instruction(buildInstruction(position))
            .build();
    }

    private SettlementInstruction buildInstruction(Position position) {
        return SettlementInstruction.builder()
            .dtcParticipantNumber(String.valueOf(position.getPositionAccount().getDtc()))
            .build();
    }

    private TermType buildTermType(Integer termId) {
        if (termId == 1) {
            return OPEN;
        } else if (termId == 2) {
            return TERM;
        }
        return null;
    }

    private SettlementType buildSettlementType(Boolean deliverFree) {
        return deliverFree == Boolean.FALSE ? DVP : FOP;
    }

    private Collateral buildCollateral(Position position) {
        return Collateral.builder()
            .contractPrice(position.getPrice())
            .collateralValue(position.getAmount())
            .currency(position.getCurrency().getCurrencyKy())
            .margin(position.getCpHaircut()) // todo check if percentage format
            .roundingRule(position.getCpMarkRoundTo())
            .roundingMode(ALWAYSUP)
            .build();
    }

    private CollateralType buildCollateralType(String collateralType) {
        if (StringUtils.isBlank(collateralType)) {
            return CollateralType.CASH;
        }
        return CollateralType.fromValue(collateralType);
    }

    private TransactingParty buildBorrowerTransactionParty(Position position) {
        return TransactingParty.builder()
            .partyRole(BORROWER)
            .party(buildBorrowerParty(position))
            .build();
    }

    private Party buildBorrowerParty(Position position) {
        return Party.builder()
            .partyId(String.valueOf(position.getPositionCpAccount().getOneSourceId()))
            .gleifLei(position.getCpLei())
            .build();
    }

    private TransactingParty buildLenderTransactionParty(Position position) {
        return TransactingParty.builder()
            .partyRole(LENDER)
            .party(buildLenderParty(position))
            .internalRef(buildInternalRef(position))
            .build();
    }

    private Party buildLenderParty(Position position) {
        return Party.builder()
            .partyId(String.valueOf(position.getPositionAccount().getOneSourceId()))
            .gleifLei(position.getAccountLei())
            .build();
    }

    private InternalReference buildInternalRef(Position position) {
        return InternalReference.builder()
            .accountId(String.valueOf(position.getPositionAccount().getAccountId()))
            .internalRefId(String.valueOf(position.getPositionId()))
            .build();
    }

    /*
     * Only rebateRate should be created. Fee rate is out of the scope.
     */
    private Rate buildRebateRateFromPosition(Position position) {
        final Index index = position.getIndex();
        if (index != null) {
            String indexName = index.getIndexName();
            if ("Fixed Rate".equalsIgnoreCase(indexName)) {
                return buildFixedRebateRateFromPosition(position);
            } else {
                return buildFloatingRebateRateFromPosition(position);
            }
        }
        return null;
    }

    private Rate buildFixedRebateRateFromPosition(Position position) {
        FixedRate fixedRate = FixedRate.builder()
            .effectiveRate(position.getRate())
            .effectiveDate(position.getAccrualDate().toLocalDate())
            .build();
        RebateRate rebateRate = RebateRate.builder()
            .fixed(fixedRate)
            .build();
        return Rate.builder()
            .rebate(rebateRate)
            .build();
    }

    private Rate buildFloatingRebateRateFromPosition(Position position) {
        final double baseRate = position.getRate() - position.getIndex().getSpread();
        FloatingRate floatingRate = FloatingRate.builder()
            .benchmark(Benchmark.valueOf(position.getIndex().getIndexName()))
            .baseRate(baseRate)
            .spread(position.getIndex().getSpread())
            .isAutoRerate(false)
            .effectiveDate(position.getAccrualDate().toLocalDate())
            .build();
        RebateRate rebateRate = RebateRate.builder()
            .floating(floatingRate)
            .build();
        return Rate.builder()
            .rebate(rebateRate)
            .build();
    }

    private Instrument buildInstrumentFromPosition(Position position) {
        final PositionSecurityDetail positionSecurityDetail = position.getPositionSecurityDetail();
        if (positionSecurityDetail != null) {
            return Instrument.builder()
                .ticker(positionSecurityDetail.getTicker())
                .cusip(positionSecurityDetail.getCusip())
                .isin(positionSecurityDetail.getIsin())
                .sedol(positionSecurityDetail.getSedol())
                .quickCode(positionSecurityDetail.getQuickCode())
                .price(buildPriceFromPosition(position))
                .build();

        }
        return null;
    }

    private Price buildPriceFromPosition(Position position) {
        // todo add priceDTO to position securityDetailDTO
        final Currency currency = position.getCurrency();
        String currencyKy = currency.getCurrencyKy();
        final PositionSecurityDetail positionSecurityDetail = position.getPositionSecurityDetail();
        Integer priceFactor = null;
        if (positionSecurityDetail != null) {
            priceFactor = positionSecurityDetail.getPriceFactor();
        }
        PriceUnit priceUnit = priceFactor != null && priceFactor == 1 ? PriceUnit.LOT : PriceUnit.SHARE;
        return Price.builder()
            .value(position.getPrice())
            .currency(currencyKy)
            .unit(priceUnit)
            .build();
    }

    private Venue buildExecutionVenueFromPosition(Position position) {
        return Venue.builder()
            .type(VenueType.OFFPLATFORM)
            .venueParties(addLenderVenueParty())
            .localVenueFields(addBorrowerLocalVenueField())
            .build();
    }

    private Set<LocalVenueField> addBorrowerLocalVenueField() {
        LocalVenueField borrowerField = LocalVenueField.builder()
            .localFieldName("BORROWER")
            .build();
        return Set.of(borrowerField);
    }

    private Set<VenueParty> addLenderVenueParty() {
        VenueParty lenderParty = VenueParty.builder()
            .partyRole(PartyRole.LENDER)
            .build();
        return Set.of(lenderParty);
    }
}
