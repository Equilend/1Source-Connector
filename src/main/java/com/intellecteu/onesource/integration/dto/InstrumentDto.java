package com.intellecteu.onesource.integration.dto;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CUSIP;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.ISIN;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUICK;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SEDOL;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwFieldMissedException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.enums.FieldSource;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class InstrumentDto implements Reconcilable {

    private Integer id;
    private String ticker;
    private String cusip;
    private String isin;
    private String sedol;
    private String quick;
    private String figi;
    private String description;
    @JsonProperty("price")
    private PriceDto price;

    @Override
    public void validateForReconciliation() throws ValidationException {
        String failedValidationFields = getFailedValidationFields();
        if (!failedValidationFields.isEmpty()) {
            throwFieldMissedException(failedValidationFields, FieldSource.ONE_SOURCE_LOAN_CONTRACT);
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
