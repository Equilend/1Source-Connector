package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.exception.ReconcileException.RECONCILE_EXCEPTION;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.ExceptionMessageDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.ReconcileException;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContractReconcileService extends OneSourceSpireReconcileService<Contract, PositionDto> {

    @Override
    public void reconcile(Contract contract, PositionDto positionDto) throws ReconcileException {
        var reconciliationFailMessages = new ArrayList<ExceptionMessageDto>();
        var tradeAgreement = contract.getTrade();
        reconciliationFailMessages.addAll(validateReconcilableObjects(contract, positionDto));
        reconciliationFailMessages.addAll(reconcileTrade(tradeAgreement, positionDto));
        if (!reconciliationFailMessages.isEmpty()) {
            reconciliationFailMessages.forEach(msg -> log.debug(msg.getExceptionMessage()));
            throw new ReconcileException(
                format(RECONCILE_EXCEPTION, contract.getContractId(), positionDto.getPositionId()),
                reconciliationFailMessages);
        }
    }
}
