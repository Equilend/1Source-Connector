package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
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
public class RateDto implements Reconcilable {

    @JsonProperty("rebate")
    private RebateRateDto rebate;
    @JsonProperty("fee")
    private FeeRateDto fee;

    @Override
    public void validateForReconciliation() throws ValidationException {
//        throwIfFieldMissedException(getRebateRate(), REBATE);
    }

    public Double retrieveRateBps() {
        return fee == null ? getRebateRate() : fee.getBaseRate();
    }

    public Double getRebateRate() {
        return rebate == null ? null : rebate.retrieveBaseRate();
    }
}
