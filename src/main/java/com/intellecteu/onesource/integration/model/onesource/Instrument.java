package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CUSIP;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.ISIN;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUICK;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SEDOL;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
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
public class Instrument implements Reconcilable {

    @JsonIgnore
    private Integer id;
    private Long securityId;
    private String ticker;
    private String cusip;
    private String isin;
    private String sedol;
    private String quickCode;
    private String figi;
    private String description;
    private Price price;
    private Integer priceFactor;

    @Override
    public void validateForReconciliation() throws ValidationException {
        String failedValidationFields = getFailedValidationFields();
        if (!failedValidationFields.isEmpty()) {
            throw new ValidationException(List.of(failedValidationFields));
        }
    }

    private String getFailedValidationFields() {
        var requiredFieldsNull = Stream.of(cusip, isin, sedol, quickCode).allMatch(Objects::isNull);
        if (requiredFieldsNull) {
            return String.join(", ", CUSIP, ISIN, SEDOL, QUICK);
        }
        return "";
    }

}
