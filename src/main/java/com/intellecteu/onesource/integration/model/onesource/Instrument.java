package com.intellecteu.onesource.integration.model.onesource;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CUSIP;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.ISIN;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUICK;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SEDOL;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwFieldMissedException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.Reconcilable;
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
    private Long id;
    private String ticker;
    private String cusip;
    private String isin;
    private String sedol;
    private String quick;
    private String figi;
    private String description;
    private Price price;

    @Override
    public void validateForReconciliation() throws ValidationException {
        String failedValidationFields = getFailedValidationFields();
        if (!failedValidationFields.isEmpty()) {
            throwFieldMissedException(failedValidationFields);
        }
    }

    private String getFailedValidationFields() {
        var isAtLeastOneInstrumentPresent = Stream.of(cusip, isin, sedol, quick)
            .anyMatch(Objects::nonNull);
        if (!isAtLeastOneInstrumentPresent) {
            return String.join(", ", CUSIP, ISIN, SEDOL, QUICK);
        }
        return "";
    }

}
