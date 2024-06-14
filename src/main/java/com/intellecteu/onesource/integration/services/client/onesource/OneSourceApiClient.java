package com.intellecteu.onesource.integration.services.client.onesource;

import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ReturnDTO;
import java.util.Optional;

public interface OneSourceApiClient {

    Optional<Agreement> findTradeAgreement(String agreementUri, EventType eventType);

    Optional<Contract> retrieveContract(String contractUri);

    RerateDTO retrieveRerate(String rerateUri);

    ReturnDTO retrieveReturn(String returnUri);
}
