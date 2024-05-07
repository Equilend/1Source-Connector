package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.model.onesource.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.LENDER;

import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.PositionSecurityDetail;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.Instrument;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MatchingService {

    public Optional<Contract> matchBorrowerPositionWithContract(Position position, Set<Contract> contracts) {
        return contracts.stream()
            .filter(c -> matchSecurityIdentifiers(c, position))
            .filter(c -> matchTradeDate(c, position))
            .filter(c -> matchQuantity(c, position))
            .filter(c -> matchCounterParty(c, position))
            .findAny(); //todo ask what to do if there are multiple matches
    }

    public Optional<Position> matchBorrowContractWithPositions(Contract contract, Set<Position> positions) {
        return positions.stream()
            .filter(p -> matchSecurityIdentifiers(contract, p))
            .filter(p -> matchTradeDate(contract, p))
            .filter(p -> matchQuantity(contract, p))
            .filter(p -> matchCounterParty(contract, p))
            .findAny();
    }

    public Optional<Contract> matchBorrowerNgtPositionWithContract(Position borrowerPosition, Set<Contract> contracts) {
        return contracts.stream()
            .filter(c -> borrowerPosition.getCustomValue2().equals(c.retrieveVenueRefKey()))
            .findAny();
    }

    private boolean matchCounterParty(Contract contract, Position position) {
        final PartyRole counterParty = IntegrationUtils.isLender(position) ? BORROWER : LENDER;
        final String oneSourceId = String.valueOf(position.getPositionCpAccount().getOneSourceId());
        final TradeAgreement trade = contract.getTrade();
        return trade.getTransactingParties().stream()
            .filter(party -> party.getPartyRole() == counterParty)
            .anyMatch(party -> Objects.equals(oneSourceId, party.getParty().getPartyId()));
    }

    private boolean matchQuantity(Contract contract, Position position) {
        final TradeAgreement trade = contract.getTrade();
        return Objects.equals(position.getQuantity().intValue(),
            trade.getQuantity());
    }

    private boolean matchTradeDate(Contract contract, Position position) {
        final TradeAgreement trade = contract.getTrade();
        final LocalDate positionTradeDate = position.getTradeDate().toLocalDate();
        return Objects.equals(trade.getTradeDate(), positionTradeDate);
    }

    private boolean matchSecurityIdentifiers(Contract contract, Position position) {
        final Instrument instrument = contract.getTrade().getInstrument();
        final PositionSecurityDetail positionSecurity = position.getPositionSecurityDetail();
        if (instrument == null || positionSecurity == null) {
            return false;
        }
        if (instrument.getCusip() == null && instrument.getIsin() == null && instrument.getSedol() == null) {
            return false;
        }
        if (positionSecurity.getCusip() == null && positionSecurity.getIsin() == null
            && positionSecurity.getSedol() == null) {
            return false;
        }
        return Objects.equals(instrument.getCusip(), positionSecurity.getCusip())
            && Objects.equals(instrument.getIsin(), positionSecurity.getIsin())
            && Objects.equals(instrument.getSedol(), positionSecurity.getSedol());
    }
}
