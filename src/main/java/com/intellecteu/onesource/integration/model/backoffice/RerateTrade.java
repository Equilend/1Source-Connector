package com.intellecteu.onesource.integration.model.backoffice;

import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.COMMA_DELIMITER;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwFieldMissedException;

import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.enums.FieldSource;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.time.LocalDateTime;
import java.util.LinkedList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RerateTrade implements Reconcilable {

    private Long tradeId;
    private LocalDateTime creationDatetime;
    private LocalDateTime lastUpdateDatetime;
    private ProcessingStatus processingStatus;
    private String matchingRerateId;
    private Long relatedPositionId;
    private String relatedContractId;
    private TradeOut tradeOut;

    @Override
    public void validateForReconciliation() throws ValidationException {
        var missedFields = new LinkedList<String>();
        if (tradeOut != null && tradeOut.getPosition() != null) {
            if (tradeOut.getRateOrSpread() == null) {
                missedFields.add("RerateTrade.tradeOut.rateOrSpread");
            }
            if (tradeOut.getAccrualDate() == null) {
                missedFields.add("RerateTrade.tradeOut.accrualDate");
            }
            if (tradeOut.getIndex() != null && tradeOut.getIndex().getIndexName() == null) {
                missedFields.add("RerateTrade.tradeOut.index.indexName");
            }
        }
        if (!missedFields.isEmpty()) {
            throwFieldMissedException(String.join(COMMA_DELIMITER, missedFields), FieldSource.ONE_SOURCE_RERATE);
        }
    }
}
