package com.intellecteu.onesource.integration.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellecteu.onesource.integration.exception.AgreementNotFoundException;
import com.intellecteu.onesource.integration.exception.ContractNotFoundException;

public interface EventService {

    void processEventData() throws JsonProcessingException, ContractNotFoundException, AgreementNotFoundException;

    void cancelContract();

    void processParties();
}
