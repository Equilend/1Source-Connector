package com.intellecteu.onesource.integration.model.onesource;

import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Return implements Reconcilable {

    private String returnId;
    private String contractId;
    private ReturnStatus returnStatus;
    private ProcessingStatus processingStatus;
    private Long matchingSpireTradeId;
    private Long relatedSpirePositionId;
    private LocalDateTime createUpdateDatetime;
    private LocalDateTime lastUpdateDatetime;
    private Venue executionVenue;
    private Integer quantity;
    private Collateral collateral;
    private SettlementType settlementType;
    private LocalDate returnSettlementDate;
    private LocalDate returnDate;
    private AcknowledgementType acknowledgementType;
    private String description;
    private List<Settlement> settlement;

    @Override
    public void validateForReconciliation() throws ValidationException {
        var missedFields = new LinkedList<String>();
        if(quantity == null) {
            missedFields.add("1sourceReturn.quantity");
        }
        if(collateral != null && collateral.getCollateralValue() == null) {
            missedFields.add("1sourceReturn.collateral.collateralValue");
        }
        if(settlementType == null) {
            missedFields.add("1sourceReturn.settlementType");
        }
        if(returnDate == null) {
            missedFields.add("1sourceReturn.returnDate");
        }
        if(returnSettlementDate == null) {
            missedFields.add("1sourceReturn.returnSettlementDate");
        }
        if (!missedFields.isEmpty()) {
            throw new ValidationException(missedFields);
        }
    }
}
