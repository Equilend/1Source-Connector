package com.intellecteu.onesource.integration.model.backoffice;


import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CUSIP;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.ISIN;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUICK;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SEDOL;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.BACKOFFICE_POSITION;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwFieldMissedException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionSecurityDetail implements Reconcilable {

    private Integer securityId;
    private String ticker;
    private String cusip;
    private String isin;
    private String sedol;
    private String quickCode;
    private String bloombergId;
    private String description;
    private Double securityPrice;
    private Integer priceFactor;

    @Override
    public void validateForReconciliation() throws ValidationException {
        String failedValidationFields = getFailedValidationFields();
        if (!failedValidationFields.isEmpty()) {
            throwFieldMissedException(failedValidationFields, BACKOFFICE_POSITION);
        }
    }

    private String getFailedValidationFields() {
        var requiredFieldsNull = Stream.of(cusip, isin, sedol, quickCode)
            .allMatch(Objects::isNull);
        if (requiredFieldsNull) {
            return String.join(", ", CUSIP, ISIN, SEDOL, QUICK);
        }
        return "";
    }
}
