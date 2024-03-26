package com.intellecteu.onesource.integration.model.backoffice;

import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.POSITION_TYPE_IS_CASH;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.BACKOFFICE_POSITION;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwIfFieldMissedException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.services.reconciliation.Reconcilable;
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
public class PositionType implements Reconcilable {

    private Integer positionTypeId;
    private String positionType;
    private Boolean isCash;

    @Override
    public void validateForReconciliation() throws ValidationException {
        throwIfFieldMissedException(isCash, POSITION_TYPE_IS_CASH, BACKOFFICE_POSITION);
    }
}
