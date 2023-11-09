package com.intellecteu.onesource.integration.services.processor.strategy.contract;

import com.intellecteu.onesource.integration.dto.CollateralDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.record.CloudEventData;
import com.intellecteu.onesource.integration.dto.record.RelatedObject;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementTempRepository;
import com.intellecteu.onesource.integration.services.OneSourceService;
import com.intellecteu.onesource.integration.services.SpireService;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.ONESOURCE_LOAN_CONTRACT_PROPOSAL;
import static com.intellecteu.onesource.integration.constant.IntegrationConstant.DomainObjects.POSITION;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.EventTypeMessage.CONTRACT_APPROVE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.EventTypeMessage.CONTRACT_CANCEL_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Generic.EventTypeMessage.CONTRACT_DECLINE_MSG;
import static com.intellecteu.onesource.integration.enums.FlowStatus.CTR_INSTRUCTIONS_RETRIEVED;
import static com.intellecteu.onesource.integration.enums.FlowStatus.POSITION_UPDATED;
import static com.intellecteu.onesource.integration.enums.FlowStatus.PROCESSED;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.GENERIC;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION;
import static com.intellecteu.onesource.integration.enums.RecordType.LOAN_CONTRACT_PROPOSAL_VALIDATED;
import static com.intellecteu.onesource.integration.model.ContractStatus.APPROVED;
import static com.intellecteu.onesource.integration.model.ContractStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_APPROVE;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_CANCEL;
import static com.intellecteu.onesource.integration.model.EventType.CONTRACT_DECLINE;
import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.DECLINED;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TO_CANCEL;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.TO_DECLINE;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.VALIDATED;
import static com.intellecteu.onesource.integration.model.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.extractPartyRole;
import static java.lang.String.format;

@Component
@Slf4j
public class ContractDataReceived extends AbstractContractProcessStrategy {
  private final AgreementRepository agreementRepository;
  private final OneSourceService oneSourceService;

  @Override
  public void process(ContractDto contract) {
      String venueRefId = contract.getTrade().getExecutionVenue().getPlatform().getVenueRefId();
      log.debug("Contract Id {} Contract Datetime {}, venueRefId: {}", contract.getContractId(),
          contract.getLastUpdateDatetime(), venueRefId);
      PositionDto position = retrievePositionByVenue(venueRefId);
      if (position == null) {
        savePositionRetrievementIssue(contract, venueRefId);
        return;
      }
      var positionLei = position.getAccountLei();
      var partyRole = extractPartyRole(contract.getTrade().getTransactingParties(), positionLei);
      log.debug("Processing contractId: {} with position: {} for party: {}", contract.getContractId(),
          position.getPositionId(), partyRole);
      if (partyRole == null) {
        processPartyRoleIssue(contract, positionLei);
        contract.setFlowStatus(PROCESSED);
        contractRepository.save(eventMapper.toContractEntity(contract));
        return;
      }
      if (partyRole == BORROWER && contract.getEventType().equals(CONTRACT)) {
        processContractForBorrower(contract, venueRefId, partyRole);
      }
      if (contract.getEventType().equals(CONTRACT_APPROVE) && contract.getContractStatus() == APPROVED) {
        processApprovedContract(contract, venueRefId, position, partyRole);
      }
      if (partyRole == BORROWER && contract.getEventType().equals(CONTRACT_CANCEL)
          && contract.getContractStatus().equals(CANCELED)) {
        processCanceledContractForBorrower(contract, position);
      }
      if (partyRole == LENDER && contract.getEventType().equals(CONTRACT_DECLINE)) {
        processDeclinedContractForLender(contract, position);
      }
    }

  private void processDeclinedContractForLender(ContractDto contract, PositionDto position) {
    String spirePositionId = position.getPositionId();
    log.warn(format(CONTRACT_DECLINE_MSG, contract.getContractId(), spirePositionId));
    contract.setProcessingStatus(DECLINED);
    String contractId = contract.getContractId(), positionId = position.getPositionId();
    recordContractEvent(RecordType.LOAN_CONTRACT_PROPOSAL_DECLINED, contractId, positionId);
    contract.setFlowStatus(PROCESSED);
    contractRepository.save(eventMapper.toContractEntity(contract));
  }

  private void processCanceledContractForBorrower(ContractDto contract, PositionDto position) {
    String spirePositionId = position.getPositionId();
    log.error("The loan contract proposal (contract identifier: {}) matching with "
            + "the SPIRE position (position identifier: {}) has been canceled by the Lender",
        contract.getContractId(), spirePositionId);
    contract.setProcessingStatus(ProcessingStatus.CANCELED);
    String contractId = contract.getContractId(), positionId = position.getPositionId();
    recordContractEvent(RecordType.LOAN_CONTRACT_PROPOSAL_CANCELED, contractId, positionId);
    contract.setFlowStatus(PROCESSED);
    contractRepository.save(eventMapper.toContractEntity(contract));
  }

  private void processApprovedContract(ContractDto contract, String venueRefId, PositionDto position, PartyRole partyRole) {
    log.debug("Retrieving Position by venueRefId: {}", venueRefId);
    spireService.updatePosition(contract, position.getPositionId());
    saveContractWithStage(contract, POSITION_UPDATED);
    updateInstruction(contract, partyRole, position, venueRefId, CTR_INSTRUCTIONS_RETRIEVED);
    String contractId = contract.getContractId(), positionId = position.getPositionId();
    recordContractEvent(RecordType.LOAN_CONTRACT_PROPOSAL_APPROVED, contractId, positionId);
    contract.setFlowStatus(PROCESSED);
    contractRepository.save(eventMapper.toContractEntity(contract));
  }

  private void processContractForBorrower(ContractDto contract, String venueRefId, PartyRole partyRole) {
    log.debug("Validating contract: {} with venueRefId: {} for party role = {}",
        contract.getContractId(), venueRefId, partyRole);
    final PositionDto position = matchContract(contract, venueRefId);
    if (contract.getProcessingStatus() == TO_DECLINE) {
      log.debug("Declining contract: {} as a {}", contract.getContractId(), partyRole);
      oneSourceService.declineContract(contract, position);
    } else if (contract.getProcessingStatus() == VALIDATED) {
      oneSourceService.updateContract(contract, position);
      oneSourceService.approveContract(contract, position);
      if (contract.isProcessedWithoutErrors()) {
        log.debug("Contract {} was approved by {}!", contract.getContractId(), partyRole);
      }
    }
    contract.setFlowStatus(PROCESSED);
    contractRepository.save(eventMapper.toContractEntity(contract));
  }

  private void recordContractEvent(RecordType recordType, String contractId, String positionId) {
    var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(GENERIC);
    var recordRequest = eventBuilder.buildRequest(contractId, recordType, positionId);
    cloudEventRecordService.record(recordRequest);
  }

  private PositionDto matchContract(ContractDto contract, String venueRefId) {
    List<Agreement> agreements = agreementRepository.findByVenueRefId(venueRefId);
    Agreement agreementOptional = null;
    if (!agreements.isEmpty()) {
      agreementOptional = agreements.get(0);
    }
    PositionDto position = retrievePositionByVenue(venueRefId);
    CollateralDto collateral = contract.getTrade().getCollateral();
    if (agreementOptional == null) {
      log.error(
          "No trade agreement has been matched with the loan contract proposal {}",
          contract.getContractId());
      contract.setProcessingStatus(TO_DECLINE);
    } else if (position != null && !position.getCpMarkRoundTo().equals(collateral.getRoundingRule())
        && collateral.getRoundingMode() != ALWAYSUP) {
      log.error(
          "The Lender's rounding rules provided it the loan contract proposal {} (contract.trade.collateral.roundingRule = {} and contract.trade.collateral.roundingMode = {}) are not matching with rounding rules recorded in SPIRE (PositionOutDTO.ExposureDTO.cpMarkRoundTo = {} - the rounding being ALWAYSUP",
          contract.getContractId(), collateral.getRoundingRule(), collateral.getRoundingMode(),
          position.getCpMarkRoundTo());
      contract.setProcessingStatus(TO_DECLINE);
    } else if (agreementOptional.getTrade().getProcessingStatus() == TO_CANCEL) {
      log.error("The loan contract proposal {} is matching a canceled trade agreement",
          contract.getContractId());
      contract.setProcessingStatus(TO_DECLINE);
      var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
      String positionId = position == null ? "null" : position.getPositionId();
      var recordRequest = eventBuilder.buildRequest(contract.getContractId(),
          LOAN_CONTRACT_PROPOSAL_MATCHING_CANCELED_POSITION, positionId);
      cloudEventRecordService.record(recordRequest);
    } else {
      contract.setProcessingStatus(VALIDATED);
      log.debug("Contract {} is validated.", contract.getContractId());
      var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
      String positionId = position == null ? "null" : position.getPositionId();
      var recordRequest = eventBuilder.buildRequest(contract.getContractId(),
          LOAN_CONTRACT_PROPOSAL_VALIDATED, positionId);
      cloudEventRecordService.record(recordRequest);
    }
    return position;
  }

  @Override
  public FlowStatus getProcessFlow() {
    return FlowStatus.TRADE_DATA_RECEIVED;
  }

  public ContractDataReceived(ContractRepository contractRepository, PositionRepository positionRepository,
      SettlementTempRepository settlementTempRepository, SpireService spireService,
      CloudEventRecordService cloudEventRecordService, EventMapper eventMapper, PositionMapper positionMapper,
      AgreementRepository agreementRepository, OneSourceService oneSourceService) {
    super(contractRepository, positionRepository, settlementTempRepository, spireService,
        cloudEventRecordService, eventMapper, positionMapper);
    this.agreementRepository = agreementRepository;
    this.oneSourceService = oneSourceService;
  }
}
