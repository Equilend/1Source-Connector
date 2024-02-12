package com.intellecteu.onesource.integration.services.client.onesource;

import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpEntity;

public interface OneSourceApiClient {

    void createContract(Agreement agreement, ContractProposal contractProposal, Position position);

    Optional<Agreement> findTradeAgreement(String agreementUri, EventType eventType);

    Optional<Contract> retrieveContract(String contractUri);

    RerateDTO retrieveRerate(String rerateUri);

    Settlement retrieveSettlementInstruction(Contract contractDto);

    void updateContract(Contract contract, HttpEntity<?> request);

    @Deprecated(since = "1.0.4")
    void approveContract(ContractDto contractDto);

    void approveContract(Contract contract, Settlement settlement);

    void declineContract(Contract contract);

    void cancelContract(Contract contract, String positionId);

    List<PartyDto> retrieveParties();

    List<TradeEvent> retrieveEvents(LocalDateTime timeSTamp);
}
