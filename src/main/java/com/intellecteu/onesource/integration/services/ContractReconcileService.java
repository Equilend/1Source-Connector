package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.exception.ReconcileException.RECONCILE_EXCEPTION;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.ExceptionMessageDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContractReconcileService extends OneSourceSpireReconcileService<ContractDto, PositionDto> {

    @Override
    public void reconcile(ContractDto contractDto, PositionDto positionDto) throws ReconcileException {
        var reconciliationFailMessages = new ArrayList<ExceptionMessageDto>();
        var tradeAgreement = contractDto.getTrade();
        reconciliationFailMessages.addAll(validateReconcilableObjects(contractDto, positionDto));
        reconciliationFailMessages.addAll(reconcileTrade(tradeAgreement, positionDto));
        if (!reconciliationFailMessages.isEmpty()) {
            reconciliationFailMessages.forEach(msg -> log.debug(msg.getExceptionMessage()));
            throw new ReconcileException(
                format(RECONCILE_EXCEPTION, contractDto.getContractId(), positionDto.getPositionId()),
                reconciliationFailMessages);
        }
    }
}
