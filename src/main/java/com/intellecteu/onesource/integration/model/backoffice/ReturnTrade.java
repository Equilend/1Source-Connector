package com.intellecteu.onesource.integration.model.backoffice;

import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.time.LocalDateTime;
import java.util.LinkedList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnTrade implements Reconcilable {
    private Long tradeId;
    private Long cancelingTradeId;
    private LocalDateTime creationDatetime;
    private LocalDateTime lastUpdateDatetime;
    private String matching1SourceReturnId;
    private Long relatedPositionId;
    private String relatedContractId;
    private TradeOut tradeOut;
    private ProcessingStatus processingStatus;

    @Override
    public void validateForReconciliation() throws ValidationException {
        var missedFields = new LinkedList<String>();
        if(tradeOut != null && tradeOut.getQuantity() == null) {
            missedFields.add("sprireReturnTrade.tradeOut.quantity");
        }
        if(tradeOut != null && tradeOut.getAmount() == null) {
            missedFields.add("sprireReturnTrade.tradeOut.amount");
        }
        if(tradeOut != null && tradeOut.getPosition().getDeliverFree() == null) {
            missedFields.add("sprireReturnTrade.tradeOut.position.deliverFree");
        }
        if(tradeOut != null && tradeOut.getTradeDate() == null) {
            missedFields.add("sprireReturnTrade.tradeOut.tradeDate");
        }
        if(tradeOut != null && tradeOut.getSettleDate() == null) {
            missedFields.add("sprireReturnTrade.tradeOut.settleDate");
        }
        if (!missedFields.isEmpty()) {
            throw new ValidationException(missedFields);
        }
    }
}
