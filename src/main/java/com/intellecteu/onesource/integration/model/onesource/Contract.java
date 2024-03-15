package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TRADE;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwIfFieldMissedException;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Contract implements Reconcilable {

    @JsonIgnore
    private Long id;
    private String contractId;
    private ProcessingStatus processingStatus;
    private String matching1SourceTradeAgreementId;
    private Long matchingSpirePositionId;
    private Long matchingSpireTradeId;
    private ContractStatus contractStatus;
    private String lastUpdatePartyId;
    @JsonAlias({"createDateTime", "createDatetime"})
    private LocalDateTime createDateTime;
    @JsonAlias({"lastUpdateDatetime", "lastUpdateDateTime"})
    private LocalDateTime lastUpdateDateTime;
    private TradeAgreement trade;
    // Expected one settlement for each party role
    private List<Settlement> settlement;

    @Override
    public void validateForReconciliation() throws ValidationException {
        throwIfFieldMissedException(trade, TRADE);
        trade.validateForReconciliation();
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
        log.debug("Updated processing status to {} for contract: {}", processingStatus, contractId);
    }

    public String retrieveCusip() {
        return isInstrumentAvailable() ? trade.getInstrument().getCusip() : "";
    }

    public String retrieveIsin() {
        return isInstrumentAvailable() ? trade.getInstrument().getIsin() : "";
    }

    public String retrieveSedol() {
        return isInstrumentAvailable() ? trade.getInstrument().getSedol() : "";
    }

    public String retrievePartyId(PartyRole partyRole) {
        return trade.getTransactingParties().stream()
            .filter(party -> party.getPartyRole() == partyRole)
            .map(TransactingParty::getParty)
            .filter(Objects::nonNull)
            .map(Party::getPartyId)
            .findAny()
            .orElse("");
    }

    private boolean isInstrumentAvailable() {
        return trade != null && trade.getInstrument() != null;
    }

}
