package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.exception.ReconcileException.RECONCILE_EXCEPTION;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ExceptionMessageDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AgreementReconcileService extends OneSourceSpireReconcileService<AgreementDto, PositionDto> {

    @Override
    public void reconcile(AgreementDto agreementDto, PositionDto positionDto) throws ReconcileException {
        var reconciliationFailMessages = new ArrayList<ExceptionMessageDto>();
        var tradeAgreement = agreementDto.getTrade();
        reconciliationFailMessages.addAll(validateReconcilableObjects(agreementDto, positionDto));
        reconciliationFailMessages.addAll(reconcileTrade(tradeAgreement, positionDto));
        if (!reconciliationFailMessages.isEmpty()) {
            String errorMsg = format(RECONCILE_EXCEPTION, agreementDto.getAgreementId(), positionDto.getId());
            throw new ReconcileException(errorMsg, reconciliationFailMessages);
        }
    }
}
