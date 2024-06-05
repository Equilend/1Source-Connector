package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.model.onesource.FixedRate.FIXED_INDEX_NAME;

import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.ContractDetails;
import com.intellecteu.onesource.integration.model.onesource.ContractProposal;
import com.intellecteu.onesource.integration.model.onesource.ContractProposalApproval;
import com.intellecteu.onesource.integration.model.onesource.Recall1Source;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.Return;
import com.intellecteu.onesource.integration.model.onesource.SettlementStatus;
import com.intellecteu.onesource.integration.model.onesource.SettlementStatusUpdate;
import com.intellecteu.onesource.integration.model.onesource.TradeEvent;
import com.intellecteu.onesource.integration.services.client.onesource.AgreementsApi;
import com.intellecteu.onesource.integration.services.client.onesource.ContractsApi;
import com.intellecteu.onesource.integration.services.client.onesource.EventsApi;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClient;
import com.intellecteu.onesource.integration.services.client.onesource.RecallsApi;
import com.intellecteu.onesource.integration.services.client.onesource.ReratesApi;
import com.intellecteu.onesource.integration.services.client.onesource.ReturnsApi;
import com.intellecteu.onesource.integration.services.client.onesource.dto.AcknowledgementTypeDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.AgreementDTO;
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
import com.intellecteu.onesource.integration.services.client.onesource.dto.PartySettlementInstructionDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RebateRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RecallDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RecallProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ReturnAcknowledgementDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ReturnDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ReturnProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.SettlementInstructionDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.SettlementStatusDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.SettlementStatusUpdateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.SettlementTypeDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenueDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenuePartiesDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenuePartyDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenueTypeDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final RecallsApi recallsApi;
    private final ContractsApi contractsApi;
    private final AgreementsApi agreementsApi;
    private final ReturnsApi returnsApi;
    private final OneSourceMapper oneSourceMapper;

    @Autowired
    public OneSourceService(OneSourceApiClient oneSourceApiClient, EventsApi eventsApi, ReratesApi reratesApi,
        RecallsApi recallsApi, ContractsApi contractsApi, AgreementsApi agreementsApi, ReturnsApi returnsApi,
        OneSourceMapper oneSourceMapper) {
        this.oneSourceApiClient = oneSourceApiClient;
        this.eventsApi = eventsApi;
        this.reratesApi = reratesApi;
        this.recallsApi = recallsApi;
        this.contractsApi = contractsApi;
        this.agreementsApi = agreementsApi;
        this.returnsApi = returnsApi;
        this.oneSourceMapper = oneSourceMapper;
    }

    public List<TradeEvent> retrieveEvents(LocalDateTime lastEventDatetime) {
        EventsDTO eventDTOS = eventsApi.ledgerEventsGet(null, null, lastEventDatetime, null, null);
        return oneSourceMapper.toTradeEventModelList(eventDTOS);
    }

    public Agreement retrieveTradeAgreementDetails(String agreementId) {
        log.debug("Sending HTTP request to get agreement details: /agreements/{}", agreementId);
        final AgreementDTO agreementDTO = agreementsApi.ledgerAgreementsAgreementIdGet(agreementId);
        return oneSourceMapper.toModel(agreementDTO);
    }

    public Contract retrieveContractDetails(String contractId) {
        log.debug("Sending HTTP request to get contract details: /contracts/{}", contractId);
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

    public Return retrieveReturn(String eventUri) {
        ReturnDTO returnDTO = oneSourceApiClient.retrieveReturn(eventUri);
        if (returnDTO != null) {
            return oneSourceMapper.toModel(returnDTO);
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }

    public void instructRerate(RerateTrade rerateTrade) {
        RerateProposalDTO body = buildRerateProposal(rerateTrade);
        reratesApi.ledgerContractsContractIdReratesPost(body,
            rerateTrade.getRelatedContractId());
    }

    public void instructRecall(String contractId, RecallProposalDTO recallProposal) {
        recallsApi.ledgerContractsContractIdRecallsPost(recallProposal, contractId);
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
        final ContractProposalDTO requestDto = buildInstructContractProposalRequestBody(contractProposal);
        log.debug("Sending a request to create a loan contract proposal.");
        final ResponseEntity<LedgerResponseDTO> response = contractsApi.ledgerContractsPostWithHttpInfo(requestDto);
        return response.getStatusCode().value() == 201;
    }

    private ContractProposalDTO buildInstructContractProposalRequestBody(ContractProposal contractProposal) {
        try {
            return oneSourceMapper.toRequestDto(contractProposal);
        } catch (Exception e) {
            log.debug("Failed to build the instruct contract proposal request body. Details:{}", e.getMessage());
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
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

    public void postReturnTrade(ReturnTrade returnTrade) {
        ReturnProposalDTO body = buildReturnProposal(returnTrade);
        returnsApi.ledgerContractsContractIdReturnsPost(body, returnTrade.getRelatedContractId());
    }

    private ReturnProposalDTO buildReturnProposal(ReturnTrade returnTrade) {
        ReturnProposalDTO returnProposalDTO = new ReturnProposalDTO();
        VenuePartiesDTO venueParties = new VenuePartiesDTO();
        venueParties.add(new VenuePartyDTO().partyRole(PartyRoleDTO.BORROWER)
            .venuePartyRefKey(String.valueOf(returnTrade.getTradeId())));
        venueParties.add(new VenuePartyDTO().partyRole(PartyRoleDTO.LENDER));
        PartySettlementInstructionDTO settlementInstructionDTO = new PartySettlementInstructionDTO()
            .partyRole(PartyRoleDTO.BORROWER)
            .settlementStatus(SettlementStatusDTO.NONE)
            .internalAcctCd(String.valueOf(returnTrade.getTradeOut().getAccount().getAccountId()))
            .instruction(new SettlementInstructionDTO().settlementBic("DTCYUS33").localAgentBic("ZYXXUS02XXX")
                .localAgentName("678XYZ").localAgentAcct("XYZ678")
                .dtcParticipantNumber(String.valueOf(returnTrade.getTradeOut().getAccount().getDtc())));
        returnProposalDTO.executionVenue(new VenueDTO().type(VenueTypeDTO.OFFPLATFORM).venueParties(venueParties))
            .quantity(returnTrade.getTradeOut().getQuantity().intValue())
            .returnDate(returnTrade.getTradeOut().getTradeDate().toLocalDate())
            .returnSettlementDate(returnTrade.getTradeOut().getSettleDate().toLocalDate())
            .collateralValue(returnTrade.getTradeOut().getAmount())
            .settlementType(returnTrade.getTradeOut().getPosition().getDeliverFree() ? SettlementTypeDTO.FOP
                : SettlementTypeDTO.DVP)
            .settlement(settlementInstructionDTO);
        return returnProposalDTO;
    }

    public void sendPositiveAck(Return oneSourceReturn, ReturnTrade returnTrade) {
        ReturnAcknowledgementDTO returnAcknowledgement = buildReturnAcknowledgement(returnTrade);
        returnsApi.ledgerContractsContractIdReturnsReturnIdAcknowledgePost(returnAcknowledgement,
            oneSourceReturn.getContractId(), oneSourceReturn.getReturnId());
    }

    private ReturnAcknowledgementDTO buildReturnAcknowledgement(ReturnTrade returnTrade) {
        ReturnAcknowledgementDTO returnAcknowledgement = new ReturnAcknowledgementDTO();
        returnAcknowledgement.acknowledgementType(AcknowledgementTypeDTO.POSITIVE)
            .settlement(new PartySettlementInstructionDTO().partyRole(PartyRoleDTO.LENDER)
                .internalAcctCd(
                    String.valueOf(returnTrade.getTradeOut().getPosition().getPositionAccount().getAccountId()))
                .instruction(new SettlementInstructionDTO().dtcParticipantNumber(
                        String.valueOf(returnTrade.getTradeOut().getPosition().getPositionAccount().getDtc()))
                    //TODO: Next values are only for demo
                    .settlementBic("DTCYUS33")
                    .localAgentBic("ZYXXUS01XXX")
                    .localAgentName("ABC1234")
                    .localAgentAcct("1234ABC")
                ));
        return returnAcknowledgement;
    }

    public void sendNegativeAck(Return oneSourceReturn, String nackReasonCode, String nackReasonText) {
        ReturnAcknowledgementDTO returnAcknowledgement = new ReturnAcknowledgementDTO();
        returnAcknowledgement.setAcknowledgementType(AcknowledgementTypeDTO.NEGATIVE);
        String description = String.format(
            "Negative Acknowledgement code: %s - Negative Acknowledgement description: %s",
            nackReasonCode, nackReasonText);
        returnAcknowledgement.setDescription(description);
        returnsApi.ledgerContractsContractIdReturnsReturnIdAcknowledgePost(returnAcknowledgement,
            oneSourceReturn.getContractId(), oneSourceReturn.getReturnId());
    }

    public Recall1Source retrieveRecallDetails(String recallId) {
        log.debug("Sending HTTP request to get recall details: /recalls/{}", recallId);
        final RecallDTO recallDTO = recallsApi.ledgerRecallsRecallIdGet(recallId);
        return oneSourceMapper.toModel(recallDTO);
    }

    public void instructRecallCancellation(String recall1SourceId, String contractId) {
        log.debug("Sending recall cancellation instruction to 1Source for recallId:{}, contractId:{} ",
            recall1SourceId, contractId);
        recallsApi.ledgerContractsContractIdRecallsRecallIdCancelPost(contractId, recall1SourceId);
    }
}
