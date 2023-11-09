package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.Reconcilable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.REBATE_BPS;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwFieldMissedException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class RateDto implements Reconcilable {

    @JsonProperty("rebate")
    private RebateRateDto rebate;
    @JsonProperty("fee")
    private FeeRateDto fee;

    @Override
    public void validateForReconciliation() throws ValidationException {
        if (retrieveRateBps() == null) {
            throwFieldMissedException(REBATE_BPS);
        }
    }

    public Double retrieveRateBps() {
        return fee == null ? getRebateRate() : fee.getBaseRate();
    }

    private Double getRebateRate() {
        return rebate == null ? null : rebate.retrieveBaseRate();
    }
}
