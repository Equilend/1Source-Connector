package com.intellecteu.onesource.integration.services.client.onesource;

import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpEntity;

@Deprecated(since = "0.0.5-SNAPSHOT", forRemoval = true)
public interface OneSourceApiClient {

    Optional<Agreement> findTradeAgreement(String agreementUri, EventType eventType);

    Optional<Contract> retrieveContract(String contractUri);

    RerateDTO retrieveRerate(String rerateUri);

    Settlement retrieveSettlementInstruction(Contract contractDto);

    void updateContract(Contract contract, HttpEntity<?> request);

    @Deprecated(since = "1.0.4")
    void approveContract(Contract contract);

    void approveContract(Contract contract, Settlement settlement);

    void declineContract(Contract contract);

    void cancelContract(Contract contract, String positionId);

    Contract cancelContract(Contract contract);

    List<PartyDto> retrieveParties();

    List<TradeEvent> retrieveEvents(LocalDateTime timeSTamp);
}
