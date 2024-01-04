package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.GET_SETTLEMENT_INSTRUCTIONS_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.POST_POSITION_UPDATE_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.POST_SETTLEMENT_INSTRUCTION_UPDATE_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.POST_POSITION_UPDATE;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.POST_SETTLEMENT_INSTRUCTION_UPDATE;
import static com.intellecteu.onesource.integration.exception.PositionCanceledException.POSITION_CANCELED_EXCEPTION;
import static com.intellecteu.onesource.integration.exception.PositionRetrievementException.TRADE_RELATED_EXCEPTION;
import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SPIRE_ISSUE;
import static com.intellecteu.onesource.integration.model.ProcessingStatus.SPIRE_POSITION_CANCELED;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.buildRequest;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createGetInstructionsNQuery;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createGetPositionNQuery;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createListOfTuplesGetPosition;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createListOfTuplesGetPositionWithoutTA;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.getDefaultHttpHeaders;
import static java.lang.String.format;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.SettlementInstructionDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.spire.AccountDto;
import com.intellecteu.onesource.integration.dto.spire.AndOr;
import com.intellecteu.onesource.integration.dto.spire.InstructionDTO;
import com.intellecteu.onesource.integration.dto.spire.LoanTradeInputDTO;
import com.intellecteu.onesource.integration.dto.spire.NQuery;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.dto.spire.PositionRequestDTO;
import com.intellecteu.onesource.integration.dto.spire.Query;
import com.intellecteu.onesource.integration.dto.spire.SwiftbicDTO;
import com.intellecteu.onesource.integration.dto.spire.TradeDTO;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.exception.PositionRetrievementException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.SettlementInstructionUpdate;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementUpdateRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SpireApiService implements SpireService {

    private final RestTemplate restTemplate;

    private final PositionRepository positionRepository;
    private final SettlementUpdateRepository settlementUpdateRepository;
    private final EventMapper eventMapper;
    private final SpireMapper spireMapper;
    private final CloudEventRecordService cloudEventRecordService;

    @Value("${spire.lenderEndpoint}")
    private String lenderSpireEndpoint;
    @Value("${spire.borrowerEndpoint}")
    private String borrowerSpireEndpoint;

    private static final String GET_POSITION_ENDPOINT = "/trades/search/position/query";
    private static final String EDIT_POSITION_ENDPOINT = "/trades/editposition";
    private static final String UPDATE_INSTRUCTION_ENDPOINT = "/rds/static/instruction/{instructionId}";
    private static final String GET_INSTRUCTION_ENDPOINT = "/rds/static/instruction";

    @Override
    public List<PositionDto> requestNewPositions(String maxPositionId) throws PositionRetrievementException {
        log.debug("Request new positions started from position id: {}", maxPositionId);
        ResponseEntity<JsonNode> response = requestPosition(
            createGetPositionNQuery(null, AndOr.AND, null,
                createListOfTuplesGetPositionWithoutTA(maxPositionId)));
        if (response.getStatusCode().is2xxSuccessful()
            && response.getBody() != null
            && !response.getBody().at("/data/beans").isMissingNode()) {
            var totalRowsCount = response.getBody().at("/data/totalRows").asText();
            if (!"0".equals(totalRowsCount)) {
                List<PositionDto> positionDtoList = new ArrayList<>();
                JsonNode positionsJsonNode = response.getBody().at("/data/beans");
                if (positionsJsonNode.isArray()) {
                    for (JsonNode positionNode : positionsJsonNode) {
                        try {
                            positionDtoList.add(spireMapper.jsonToPositionDto(positionNode));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Wrong structure of PositionDto.class");
                        }
                    }
                }
                return positionDtoList;
            }
        }
        return List.of();
    }

    @Override
    public PositionDto requestPositionByVenueRefId(String venueRefId)
        throws PositionRetrievementException {
        ResponseEntity<JsonNode> response = requestPosition(createGetPositionNQuery(null, AndOr.AND, true,
            createListOfTuplesGetPosition("customValue2", "EQUALS", venueRefId, null)));
        if (response.getStatusCode().is2xxSuccessful()
            && response.getBody() != null && !response.getBody().at("/data/beans/0").isMissingNode()) {
            JsonNode positionJsonNode = response.getBody().at("/data/beans/0");
            try {
                return spireMapper.jsonToPositionDto(positionJsonNode);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Wrong structure of PositionDto.class");
            }
        } else {
            var exceptionMessage = response.getBody() == null ? "No body" : response.getBody().toString();
            throw new PositionRetrievementException(exceptionMessage);
        }
    }

    @Override
    public PositionDto getTradePosition(AgreementDto agreement) {
        TradeAgreementDto trade = agreement.getTrade();
        String venueRefId = trade.getExecutionVenue().getVenueRefKey();
        if (StringUtils.isEmpty(venueRefId)) {
            return null;
        }
        log.debug("Retrieving Spire Position by venueRefId={}", venueRefId);
        ResponseEntity<JsonNode> response = requestPosition(createGetPositionNQuery(null, AndOr.AND, true,
            createListOfTuplesGetPosition("customValue2", "EQUALS", venueRefId, null)));
        validateResponse(response, venueRefId, trade);
        final Position position = savePosition(response, venueRefId, trade);
        if (position == null) {
            return null;
        }
        if (position.getPositionStatus() == null || position.getPositionStatus().getStatus().equals("CANCELED")) {
            var msg = format(POSITION_CANCELED_EXCEPTION, venueRefId, trade.retrieveVenueName(),
                trade.getTradeDate());
            saveIssue(msg, trade, SPIRE_POSITION_CANCELED);
            return null;
        }
        return spireMapper.toPositionDto(position);
    }

    private void validateResponse(ResponseEntity<JsonNode> response, String venueRefId, TradeAgreementDto trade) {
        if (response.getStatusCode() == NOT_FOUND) {
            var msg = format("The position related to the trade : %s negotiated on %s on %s "
                + "has not been recorded in SPIRE", venueRefId, trade.retrieveVenueName(), trade.getTradeDate());
            saveIssue(msg, trade, SPIRE_ISSUE);
        } else if (response.getStatusCode() == UNAUTHORIZED || response.getStatusCode() == FORBIDDEN) {
            var msg = format(TRADE_RELATED_EXCEPTION, venueRefId, trade.retrieveVenueName(),
                trade.getTradeDate(), response.getStatusCode());
            saveIssue(msg, trade, SPIRE_ISSUE);
        }
    }

    private static void saveIssue(String errorMsg, TradeAgreementDto trade, ProcessingStatus status) {
        log.error(errorMsg);
        trade.setProcessingStatus(status);
    }

    private Position extractPositionFromJson(ResponseEntity<JsonNode> response) throws JsonProcessingException {
        if (response.getBody() != null
            && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            var totalRows = response.getBody().at("/data/totalRows").asText();
            if (!"0".equals(totalRows)) {
                JsonNode jsonNode = response.getBody().get("data").get("beans").get(0);
                return spireMapper.toPosition(jsonNode);
            }
        }
        return null;
    }

    @Override
    public ResponseEntity<JsonNode> requestPosition(NQuery query) {
        final HttpEntity<Query> request = buildRequest(query);

        var lenderResponse = requestPosition(request, lenderSpireEndpoint + GET_POSITION_ENDPOINT, LENDER);
        if (isPositionFound(lenderResponse)) {
            return lenderResponse;
        }
        var borrowerResp = requestPosition(request, borrowerSpireEndpoint + GET_POSITION_ENDPOINT, BORROWER);
        if (isPositionFound(borrowerResp)) {
            return borrowerResp;
        }
        return borrowerResp; // temporary solution instead of returning an empty response
    }

    private ResponseEntity<JsonNode> requestPosition(HttpEntity<Query> request, String endpoint, PartyRole role) {
        log.debug("Sending POST request to {}: {}", role, endpoint);
        return sendSpireHttpRequest(request, endpoint);
    }

    private ResponseEntity<JsonNode> sendSpireHttpRequest(HttpEntity<Query> request,
        String endpoint) throws HttpStatusCodeException {
        return restTemplate.postForEntity(endpoint, request, JsonNode.class);
    }

    private boolean isPositionFound(ResponseEntity<JsonNode> response) {
        if (response != null && response.getBody() != null && response.getBody().get("data") != null) {
            String rows = response.getBody().get("data").get("totalRows").asText();
            log.debug("Response returned {} rows", rows);
            return Integer.parseInt(rows) > 0;
        }
        return false;
    }

    private HttpEntity<LoanTradeInputDTO> createRequest(ContractDto contract, String positionId) {
        LoanTradeInputDTO loanTradeInputDTO = LoanTradeInputDTO.builder()
            .originalTrade(TradeDTO.builder()
                .positionId(Integer.valueOf(positionId))
                .positionDTO(new PositionRequestDTO(contract.getContractId()))  // todo check this request
                .build())
            .build();

        final var headers = getDefaultHttpHeaders();
        return new HttpEntity<>(loanTradeInputDTO, headers);
    }

    @Deprecated(since = "1.0.4") // todo discuss the get instruction body changes
    @Override
    public List<SettlementDto> retrieveSettlementDetails(PositionDto position, String venueRefId,
        TradeAgreementDto trade,
        PartyRole roleForRequest) {
        final HttpEntity<Query> request = buildRequest(createGetInstructionsNQuery(position));

        log.debug("Sending POST request to {}", lenderSpireEndpoint + GET_INSTRUCTION_ENDPOINT);
        ResponseEntity<JsonNode> response = null;
        if (roleForRequest == LENDER) {
            response = requestInstruction(position.getPositionId(), position.getPositionId(),
                request, lenderSpireEndpoint + GET_INSTRUCTION_ENDPOINT);
        }
        if (roleForRequest == BORROWER) {
            response = requestInstruction(position.getPositionId(), position.getPositionId(),
                request, borrowerSpireEndpoint + GET_INSTRUCTION_ENDPOINT);
        }

        if (trade != null) { // todo rework
            processFailedResponse(position, venueRefId, trade, response);
        }

        if (response != null && response.getBody() != null && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            var totalRows = response.getBody().at("/data/totalRows").asText();
            if (!"0".equals(totalRows)) {
                JsonNode jsonNode = response.getBody().get("data").get("beans").get(0);

                return retrieveInstructions(jsonNode, roleForRequest, venueRefId);
            }
        }
        return null;
    }

    @Override
    public ResponseEntity<SettlementDto> requestLenderSettlementDetails(PositionDto position,
        HttpEntity<Query> request) throws RestClientException {
        final String endpoint = lenderSpireEndpoint + GET_INSTRUCTION_ENDPOINT;
        return executePostForSettlementInstructionDetails(position, endpoint, request, LENDER);
    }

    @Override
    public ResponseEntity<SettlementDto> requestBorrowerSettlementDetails(PositionDto position,
        HttpEntity<Query> request) throws RestClientException {
        final String endpoint = borrowerSpireEndpoint + GET_INSTRUCTION_ENDPOINT;
        return executePostForSettlementInstructionDetails(position, endpoint, request, BORROWER);
    }

    // todo reword request process and json parsing after a new architecture flow implemented
    private ResponseEntity<SettlementDto> executePostForSettlementInstructionDetails(PositionDto position,
        String endpoint,
        HttpEntity<Query> request, PartyRole partyRole) throws RestClientException {
        log.debug("Sending POST request to {}", endpoint);
        var response = restTemplate.postForEntity(endpoint, request, JsonNode.class);
        if (response.getBody() != null && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            var totalRows = response.getBody().at("/data/totalRows").asText();
            if (!"0".equals(totalRows)) {
                JsonNode jsonNode = response.getBody().get("data").get("beans").get(0);

                final List<SettlementDto> settlementList = retrieveInstructions(jsonNode, partyRole,
                    position.getCustomValue2());
                if (!settlementList.isEmpty()) {
                    return new ResponseEntity<>(settlementList.get(0), response.getHeaders(), response.getStatusCode());
                }
            }
        }
        throw new HttpClientErrorException(NOT_FOUND);
    }

    private ResponseEntity<JsonNode> requestInstruction(String agreementId, String positionId,
        HttpEntity<Query> request, String endpoint) {
        try {
            return sendSpireHttpRequest(request, endpoint);
        } catch (HttpStatusCodeException e) {
            log.warn("SPIRE error response for request Instruction: " + e.getStatusCode());
            if (Set.of(UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                var recordRequest = eventBuilder.buildExceptionRequest(agreementId, e,
                    IntegrationSubProcess.GET_SETTLEMENT_INSTRUCTIONS, positionId);
                cloudEventRecordService.record(recordRequest);
            }
            return null;
        }
    }

    @Override
    public void updatePosition(ContractDto contract, String positionId) {
        HttpEntity<LoanTradeInputDTO> request = createRequest(contract, positionId);
        log.debug("Updating SPIRE position {}. Sending POST request to {}",
            positionId, lenderSpireEndpoint + EDIT_POSITION_ENDPOINT);
        try {
            restTemplate.postForEntity(lenderSpireEndpoint + EDIT_POSITION_ENDPOINT, request, JsonNode.class);
            log.debug("Spire Position id:{} for contract id:{} was updated!", positionId, contract.getContractId());
        } catch (HttpStatusCodeException e) {
            log.error(format(POST_POSITION_UPDATE_EXCEPTION_MSG, contract.getContractId(), positionId, e.getMessage()));
            contract.setProcessingStatus(SPIRE_ISSUE);
            if (Set.of(UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                var recordRequest = eventBuilder.buildExceptionRequest(contract.getContractId(), e,
                    POST_POSITION_UPDATE, contract.getMatchingSpirePositionId());
                cloudEventRecordService.record(recordRequest);
            }
        }
    }

    @Deprecated(since = "Flow II") //todo refine on refactoring
    @Override
    public void updateInstruction(ContractDto contract, PositionDto position, String venueRefId,
        SettlementInstructionDto settlementInstructionDto, PartyRole role) {
        InstructionDTO instructionDTO = fillInstructions(settlementInstructionDto);

        SettlementInstructionUpdate settlementInstructionUpdate = settlementUpdateRepository
            .findByVenueRefId(venueRefId).get(0);

        HttpEntity<InstructionDTO> request = new HttpEntity<>(instructionDTO, getDefaultHttpHeaders());

        log.debug("Update instruction for contract: {}, position: {}, venueRefId: {}, party: {}",
            contract.getContractId(), position.getPositionId(), venueRefId, role);
        String url = switch (role) {
            case LENDER -> lenderSpireEndpoint + UPDATE_INSTRUCTION_ENDPOINT;
            case BORROWER -> borrowerSpireEndpoint + UPDATE_INSTRUCTION_ENDPOINT;
            default -> "";
        };
        executeUpdateInstructionRequest(url, contract, request, settlementInstructionUpdate);
        log.debug("The Spire settlement instruction was updated! The loan contract: {}, Spire position: {}",
            contract.getContractId(), position.getPositionId());
    }

    @Override
    public void updateInstruction(InstructionDTO instructionDto, SettlementDto settlementDto, PartyRole role) {
        HttpEntity<InstructionDTO> request = new HttpEntity<>(instructionDto, getDefaultHttpHeaders());
        String url = switch (role) {
            case LENDER -> lenderSpireEndpoint + UPDATE_INSTRUCTION_ENDPOINT;
            case BORROWER -> borrowerSpireEndpoint + UPDATE_INSTRUCTION_ENDPOINT;
            default -> throw new HttpClientErrorException(NOT_FOUND);
        };
        final Integer instructionId = settlementDto.getInstructionId();
        log.debug("Executing PUT request to Update Settlement Instruction: {}, url: {}", instructionId, url);
        restTemplate.exchange(url, PUT, request, JsonNode.class, instructionId);
    }

    private ResponseEntity<JsonNode> executeUpdateInstructionRequest(String url, ContractDto contract,
        HttpEntity<InstructionDTO> request, SettlementInstructionUpdate settlementInstructionUpdate) {
        try {
            return restTemplate.exchange(url, PUT, request, JsonNode.class,
                settlementInstructionUpdate.getInstructionId());
        } catch (HttpStatusCodeException e) {
            String contractId = contract.getContractId();
            log.error(format(POST_SETTLEMENT_INSTRUCTION_UPDATE_EXCEPTION_MSG, contractId,
                contract.getMatchingSpirePositionId(), e.getStatusCode()));
            contract.setProcessingStatus(SPIRE_ISSUE);
            if (Set.of(UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(e.getStatusCode())) {
                var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
                var recordRequest = eventBuilder.buildExceptionRequest(contractId, e,
                    POST_SETTLEMENT_INSTRUCTION_UPDATE, contract.getMatchingSpirePositionId());
                cloudEventRecordService.record(recordRequest);
            }
            return null;
        }
    }

    // todo rework as only one Settlement Instruction expected either for Borrower of for Lender
    private List<SettlementDto> retrieveInstructions(JsonNode jsonNode, PartyRole partyRole, String venueRefId) {
        List<SettlementDto> settlements = new ArrayList<>();
        JsonNode bicDTO = jsonNode.get("bicDTO");
        JsonNode agentBicDTO = jsonNode.get("agentBicDTO");
        JsonNode accountDTO = jsonNode.get("accountDTO");

        Integer instructionId = Integer.valueOf(eventMapper.getIfExist(jsonNode, "instructionId"));
        String bic = eventMapper.getIfExist(bicDTO, "bic");
        String name = eventMapper.getIfExist(jsonNode, "agentName");
        String agentBic = eventMapper.getIfExist(agentBicDTO, "bic") + eventMapper.getIfExist(agentBicDTO, "branch");
        String agentAcc = eventMapper.getIfExist(jsonNode, "agentSafe");
        String dtc = eventMapper.getIfExist(accountDTO, "dtc");
        SettlementInstructionDto settlementInstruction = toSettlementInstruction(bic, name, agentBic, agentAcc, dtc);
        SettlementDto settlementDto = SettlementDto.builder()
            .partyRole(partyRole)
            .instructionId(instructionId)
            .instruction(settlementInstruction).build();

        saveSettlementUpdate(partyRole, venueRefId, instructionId, settlementInstruction);

        settlements.add(settlementDto);
        log.debug("Retrieved {} settlement instructions for {} with venueRefId={}",
            settlements.size(), partyRole, venueRefId);

        return settlements;
    }

    private void saveSettlementUpdate(PartyRole partyRole, String venueRefId, Integer instructionId,
        SettlementInstructionDto settlementInstruction) {
        SettlementInstructionUpdate settlementInstructionUpdate = SettlementInstructionUpdate.builder()
            .instructionId(instructionId)
            .venueRefId(venueRefId)
            .partyRole(partyRole)
            .instruction(eventMapper.toInstructionEntity(settlementInstruction)).build();

        settlementUpdateRepository.save(settlementInstructionUpdate);
    }

    private Position savePosition(ResponseEntity<JsonNode> response, String venueRefId,
        TradeAgreementDto trade) {
        try {
            final Position entity = extractPositionFromJson(response);
            if (entity != null) {
                entity.setVenueRefId(venueRefId);
                entity.setLastUpdateDateTime(LocalDateTime.now());
                return positionRepository.save(entity);
            }
        } catch (JsonProcessingException | NullPointerException e) {
            var msg = format(TRADE_RELATED_EXCEPTION, venueRefId, trade.retrieveVenueName(),
                trade.getTradeDate(), "Parse data exception!");
            saveIssue(msg, trade, SPIRE_ISSUE);
        }
        return null;
    }

    SettlementInstructionDto toSettlementInstruction(String bic, String name, String agentBic, String agentAcc,
        String dtc) {
        return SettlementInstructionDto.builder()
            .settlementBic(bic)
            .localAgentName(name)
            .localAgentBic(agentBic)
            .localAgentAcct(agentAcc)
            .dtcParticipantNumber(dtc)
            .build();
    }

    private static InstructionDTO fillInstructions(SettlementInstructionDto settlementInstruction) {
        final AccountDto accountDTO = new AccountDto();
        accountDTO.setDtc(Long.valueOf(settlementInstruction.getDtcParticipantNumber()));
        return InstructionDTO.builder()
            .agentName(settlementInstruction.getLocalAgentName())
            .agentSafe(settlementInstruction.getLocalAgentAcct())
            .accountDTO(accountDTO)
            .agentBicDTO(
                new SwiftbicDTO(settlementInstruction.getSettlementBic(), settlementInstruction.getLocalAgentBic()))
            .build();
    }

    private static void processFailedResponse(PositionDto position, String venueRefId, TradeAgreementDto trade,
        ResponseEntity<JsonNode> response) {
        if (response == null || response.getStatusCode() != OK) {
            var responseCode = response == null ? "no response" : response.getStatusCode();
            log.error(format(GET_SETTLEMENT_INSTRUCTIONS_EXCEPTION_MSG, position.getPositionId(), responseCode));
            trade.setProcessingStatus(SPIRE_ISSUE);
        }
    }

    public SpireApiService(RestTemplate restTemplate, PositionRepository positionRepository, EventMapper eventMapper,
        SettlementUpdateRepository settlementUpdateRepository, SpireMapper spireMapper,
        CloudEventRecordService cloudEventRecordService) {
        this.restTemplate = restTemplate;
        this.positionRepository = positionRepository;
        this.settlementUpdateRepository = settlementUpdateRepository;
        this.eventMapper = eventMapper;
        this.spireMapper = spireMapper;
        this.cloudEventRecordService = cloudEventRecordService;
    }
}
