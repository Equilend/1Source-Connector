package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeEventDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.EventType;

import java.time.LocalDateTime;
import java.util.List;

public interface OneSourceService {

    void createContract(AgreementDto agreement, List<SettlementDto> settlement, PositionDto position);

    AgreementDto findTradeAgreement(String agreementId, EventType eventType);

    ContractDto findContract(String contractId);

    void updateContract(ContractDto contractDto, PositionDto positionDto);

    void approveContract(ContractDto contractDto, PositionDto positionDto);

    void declineContract(ContractDto contractDto, PositionDto positionDto);

    void cancelContract(Contract contract, String positionId);

    List<PartyDto> retrieveParties();

    List<TradeEventDto> retrieveEvents(LocalDateTime timeSTamp);
}
