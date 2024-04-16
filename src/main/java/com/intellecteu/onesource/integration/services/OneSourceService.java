package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.model.onesource.FixedRate.FIXED_INDEX_NAME;

import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractDetails;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.ContractProposalApproval;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.SettlementStatus;
import com.intellecteu.onesource.integration.model.onesource.SettlementStatusUpdate;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.client.onesource.ContractsApi;
import com.intellecteu.onesource.integration.services.client.onesource.EventsApi;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClient;
import com.intellecteu.onesource.integration.services.client.onesource.ReratesApi;
import com.intellecteu.onesource.integration.services.client.onesource.dto.BenchmarkCdDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractProposalApprovalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.EventsDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FixedRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FixedRateDefDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FloatingRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FloatingRateDefDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.LedgerResponseDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.PartyRoleDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RebateRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.SettlementStatusUpdateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenueDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenuePartiesDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenuePartyDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenueTypeDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
@Transactional(readOnly = true)
public class OneSourceService {

    private final OneSourceApiClient oneSourceApiClient;
    private final EventsApi eventsApi;
    private final ReratesApi reratesApi;
    private final ContractsApi contractsApi;
    private final OneSourceMapper oneSourceMapper;

    @Autowired
    public OneSourceService(OneSourceApiClient oneSourceApiClient, EventsApi eventsApi, ReratesApi reratesApi,
        OneSourceMapper oneSourceMapper,
        @Value("${onesource.base-endpoint}") String onesourceBasePath,
        @Value("${onesource.version}") String onesourceVersion,
        ContractsApi contractsApi) {
        this.oneSourceApiClient = oneSourceApiClient;
        this.eventsApi = eventsApi;
        this.reratesApi = reratesApi;
        this.contractsApi = contractsApi;
        this.contractsApi.getApiClient().setBasePath(onesourceBasePath.concat(onesourceVersion));
        this.reratesApi.getApiClient().setBasePath(onesourceBasePath.concat(onesourceVersion));
        this.oneSourceMapper = oneSourceMapper;
    }

    public List<TradeEvent> retrieveEvents(LocalDateTime lastEventDatetime) {
        EventsDTO eventDTOS = eventsApi.ledgerEventsGet(null, null, lastEventDatetime, null, null);
        return oneSourceMapper.toTradeEventModelList(eventDTOS);
    }

    public Optional<Agreement> retrieveTradeAgreement(String eventUri, EventType eventType) {
        return oneSourceApiClient.findTradeAgreement(eventUri, eventType);
    }

    public Contract retrieveContractDetails(String contractId) {
        log.debug("Sending HTTP request to get contract details for contract id = {}", contractId);
        final ContractDTO contractDetailsResponse = contractsApi.ledgerContractsContractIdGet(contractId);
        ContractDetails contractDetails = oneSourceMapper.toModel(contractDetailsResponse);
        return oneSourceMapper.toModel(contractDetails);
    }

    public boolean executeContractProposalApproval(ContractProposalApproval approval, Contract contract)
        throws RestClientException {
        ContractProposalApprovalDTO approvalRequest = oneSourceMapper.toRequestDto(approval);
        contractsApi.ledgerContractsContractIdApprovePost(approvalRequest, contract.getContractId());
        log.debug("Contract id: {} approval was sent.", contract.getContractId());
        return true;
    }

    public Rerate retrieveRerate(String eventUri) {
        RerateDTO rerateDTO = oneSourceApiClient.retrieveRerate(eventUri);
        if (rerateDTO != null) {
            return oneSourceMapper.toModel(rerateDTO);
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }

    public void instructRerate(RerateTrade rerateTrade) {
        RerateProposalDTO body = buildRerateProposal(rerateTrade);
        reratesApi.ledgerContractsContractIdReratesPost(body,
            rerateTrade.getRelatedContractId());
    }

    public void approveRerate(String contractId, String rerateId) {
        reratesApi.ledgerContractsContractIdReratesRerateIdApprovePostWithHttpInfo(
            contractId, rerateId);
    }

    public void declineRerate(String contractId, String rerateId) {
        reratesApi.ledgerContractsContractIdReratesRerateIdDeclinePostWithHttpInfo(
            contractId, rerateId);
    }

    public void cancelRerate(String contractId, String rerateId) {
        reratesApi.ledgerContractsContractIdReratesRerateIdCancelPostWithHttpInfo(
            contractId, rerateId);
    }

    private RerateProposalDTO buildRerateProposal(RerateTrade rerateTrade) {
        RerateProposalDTO rerateProposalDTO = new RerateProposalDTO();
        RebateRateDTO rebateRate = new RebateRateDTO();
        if (FIXED_INDEX_NAME.equals(rerateTrade.getTradeOut().getPosition().getIndex().getIndexName())) {
            FixedRateDTO rebate = new FixedRateDTO();
            rebate.fixed(new FixedRateDefDTO()
                .baseRate(rerateTrade.getTradeOut().getRateOrSpread())
                .effectiveDate(rerateTrade.getTradeOut().getAccrualDate().toLocalDate())
            );
            rebateRate.rebate(rebate);
        } else {
            FloatingRateDTO rebate = new FloatingRateDTO();
            rebate.floating(new FloatingRateDefDTO()
                .benchmark(BenchmarkCdDTO.valueOf(rerateTrade.getTradeOut().getIndex().getIndexName()))
                .spread(rerateTrade.getTradeOut().getRateOrSpread())
                .isAutoRerate(false)
                .effectiveDate(rerateTrade.getTradeOut().getAccrualDate().toLocalDate())
            );
            rebateRate.rebate(rebate);
        }
        VenuePartiesDTO venuePartyDTOS = new VenuePartiesDTO();
        venuePartyDTOS.add(new VenuePartyDTO().partyRole(PartyRoleDTO.LENDER));
        venuePartyDTOS.add(new VenuePartyDTO().partyRole(PartyRoleDTO.BORROWER));
        rerateProposalDTO.executionVenue(new VenueDTO().type(VenueTypeDTO.OFFPLATFORM).venueParties(venuePartyDTOS));
        rerateProposalDTO.rate(rebateRate);
        return rerateProposalDTO;
    }

    /**
     * Send new contract proposal request to OneSource for inclusion in the ledger
     *
     * @param contractProposal ContractProposal
     * @return true if HTTP response code is CREATED (201)
     * @throws RestClientException if response status is 4xx or 5xx
     */
    public boolean instructLoanContractProposal(ContractProposal contractProposal)
        throws RestClientException {
        final ContractProposalDTO requestDto = oneSourceMapper.toRequestDto(contractProposal);
        log.debug("Sending a request to create a loan contract proposal.");
        final ResponseEntity<LedgerResponseDTO> response = contractsApi.ledgerContractsPostWithHttpInfo(requestDto);
        return response.getStatusCode().value() == 201;
    }

    public boolean instructDeclineLoanProposal(Contract contract) throws RestClientException {
        log.debug("Sending request to decline a loan contract proposal with id = {}.", contract.getContractId());
        contractsApi.ledgerContractsContractIdDeclinePost(contract.getContractId());
        return true;
    }

    public boolean instructCancelLoanContract(String contractId) throws RestClientException {
        log.debug("Sending request to cancel a loan contract id = {}.", contractId);
        contractsApi.ledgerContractsContractIdCancelPost(contractId);
        return true;
    }

    public void instructUpdateSettlementStatus(Position position, SettlementStatus settlementStatus) {
        String contractId = position.getMatching1SourceLoanContractId();
        log.debug("Sending request to update contract={} with settlement status={}.",
            contractId, settlementStatus.name());
        SettlementStatusUpdate settlementStatusUpdate = new SettlementStatusUpdate(settlementStatus);
        final SettlementStatusUpdateDTO requestDto = oneSourceMapper.toRequestDto(settlementStatusUpdate);
        contractsApi.ledgerContractsContractIdPatch(contractId, requestDto);
    }

}
