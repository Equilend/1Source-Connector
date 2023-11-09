package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.Reconcilable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.stream.Stream;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.CUSIP;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.FIGI;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.ISIN;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.QUICK;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.SEDOL;
import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TICKER;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwFieldMissedException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class InstrumentDto implements Reconcilable {
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
        var failedValidationFields = getFailedValidationFields();
        if (!failedValidationFields.isEmpty()) {
            throwFieldMissedException(failedValidationFields);
        }
    }

    private String getFailedValidationFields() {
        var isAtLeastOneInstrumentPresent = Stream.of(ticker, cusip, isin, sedol, quick, figi)
            .anyMatch(Objects::nonNull);
        if (!isAtLeastOneInstrumentPresent) {
            return String.join(", ", TICKER, CUSIP, ISIN, SEDOL, QUICK, FIGI);
        }
        return "";
    }
}
