package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.constant.PositionConstant.BORROWER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.COMMA_DELIMITER;
import static com.intellecteu.onesource.integration.constant.PositionConstant.LENDER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.CANCEL_LOAN;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.CANCEL_NEW_BORROW;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.NEW_BORROW;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.NEW_LOAN;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.PENDING_ONESOURCE_CONFIRMATION;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.POSITION_ID;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.RERATE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.RERATE_BORROW;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.ROLL_BORROW;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.ROLL_LOAN;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.STATUS;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.TRADE_ID;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.TRADE_STATUS;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Request.TRADE_TYPE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_UPDATED_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_POSITION_UPDATE;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.CANCELLED;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.OPEN;
import static com.intellecteu.onesource.integration.services.client.spire.dto.NQueryTuple.OperatorEnum.EQUALS;
import static com.intellecteu.onesource.integration.services.client.spire.dto.NQueryTuple.OperatorEnum.GREATER_THAN;
import static com.intellecteu.onesource.integration.services.client.spire.dto.NQueryTuple.OperatorEnum.IN;
import static java.lang.String.join;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.exception.InstructionRetrievementException;
import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.PositionConfirmationRequest;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.model.backoffice.TradeOut;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.PositionStatusEnum;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.SettlementInstruction;
import com.intellecteu.onesource.integration.services.client.spire.InstructionSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.PositionSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.TradeSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.dto.AccountDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.LoanTradeInputDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQuery;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryRequest;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryTuple;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryTuple.OperatorEnum;
import com.intellecteu.onesource.integration.services.client.spire.dto.OneSourceConfimationDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponseInstructionDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponsePositionOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponseTradeOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponseTradeOutDTO.StatusEnum;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponsePositionDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SwiftbicDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.TradeDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.TradeOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.instruction.InstructionDTO;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

//@Service initiated in @see com.intellecteu.onesource.integration.config.AppConfig
@Slf4j
public class BackOfficeService {

    private static final String STARTING_POSITION_ID = "0";
    private static final Long STARTING_TRADE_ID = 0L;
    private static final List<String> positionTypes = List.of(LENDER_POSITION_TYPE, BORROWER_POSITION_TYPE);

    private final PositionSpireApiClient positionSpireApiClient;
    private final TradeSpireApiClient tradeSpireApiClient;
    private final InstructionSpireApiClient instructionClient;
    private final Integer userId;
    private final String userName;
    private final BackOfficeMapper backOfficeMapper;
    private final CloudEventRecordService cloudEventRecordService;

    public void instructUpdatePosition(PositionConfirmationRequest confirmationRequest) {
        final OneSourceConfimationDTO confirmationDto = backOfficeMapper.toRequestDto(confirmationRequest);
        log.debug("Sending confirmation request to SPIRE for position: {} ", confirmationRequest.getPositionId());
        tradeSpireApiClient.confirmAndBatchPendingPositionsUsingPOST(null, confirmationDto);
    }

    public List<Position> getNewSpirePositions(String lastTradeId) {
        NQuery nQuery = new NQuery()
            .andOr(NQuery.AndOrEnum.AND)
            .tuples(createTuplesGetNewTradePositions(lastTradeId));
        NQueryRequest nQueryRequest = new NQueryRequest().nQuery(nQuery);
        try {
            log.debug("Sending request with SPIRE API Client");
            final ResponseEntity<SResponseNQueryResponseTradeOutDTO> response = tradeSpireApiClient.getTrades(
                nQueryRequest);
            if (responseTradeHasData(response)) {
                List<TradeOutDTO> responseGroups = response.getBody().getData().getBeans();
                return responseGroups.stream()
                    .map(backOfficeMapper::toPositionModel)
                    .toList();
            }
        } catch (RestClientException e) {
            recordRestClientException(e, GET_NEW_POSITIONS_PENDING_CONFIRMATION);
        }
        return List.of();
    }

    private void recordRestClientException(RestClientException e, IntegrationSubProcess subProcess) {
        log.warn("Rest client exception: {}", e.getMessage());
        if (e instanceof HttpStatusCodeException exception) {
            final HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
            log.warn("SPIRE error response for {} subprocess. Details: {}", subProcess, status.value());
            recordPositionExceptionEvent(exception, CONTRACT_INITIATION, subProcess);
        }
    }

    public List<TradeOut> fetchUpdatesOnPositions(List<Position> positions) {
        final NQueryRequest nQueryRequest = buildRequestForUpdates(positions);
        try {
            log.debug("Sending request with SPIRE API Client");
            ResponseEntity<SResponseNQueryResponseTradeOutDTO> response = tradeSpireApiClient.getTrades(nQueryRequest);
            if (responseTradeHasData(response)) {
                return convertResponseToTrades(response.getBody().getData().getBeans());
            }
        } catch (RestClientException exception) {
            recordRestClientException(exception, GET_UPDATED_POSITIONS_PENDING_CONFIRMATION);
        }
        return List.of();
    }

    private List<TradeOut> convertResponseToTrades(List<TradeOutDTO> responseBeans) {
        List<TradeOut> tradesWithUpdatedPositions = responseBeans.stream()
            .map(backOfficeMapper::toModel)
            .toList();
        log.debug("Found {} trades with updated positions", tradesWithUpdatedPositions.size());
        return tradesWithUpdatedPositions;
    }

    private NQueryRequest buildRequestForUpdates(List<Position> positions) {
        Long lastTradeIdRecorded = retrieveLatestTradeId(positions);
        String commaSeparatedIdList = positions.stream()
            .map(p -> String.valueOf(p.getPositionId()))
            .collect(Collectors.joining(COMMA_DELIMITER));
        NQuery nQuery = new NQuery().andOr(NQuery.AndOrEnum.AND).tuples(
            createTuplesGetUpdatePositionsByIdList(lastTradeIdRecorded, commaSeparatedIdList));
        return new NQueryRequest().nQuery(nQuery);
    }

    private static Long retrieveLatestTradeId(List<Position> positions) {
        return positions.stream()
            .map(Position::getTradeId)
            .filter(Objects::nonNull)
            .max(Comparator.naturalOrder())
            .orElse(STARTING_TRADE_ID);
    }

    private boolean responseTradeHasData(ResponseEntity<SResponseNQueryResponseTradeOutDTO> response) {
        return response != null && response.getBody() != null && response.getBody().getData() != null
            && !response.getBody().getData().getBeans().isEmpty();
    }

    private boolean responseHasData(ResponseEntity<SResponseNQueryResponsePositionOutDTO> response) {
        return response.getBody() != null && response.getBody().getData() != null
            && response.getBody().getData().getTotalRows() != null
            && response.getBody().getData().getTotalRows() > 0;
    }

    public List<Position> retrieveUpdatedOpenedPositions(List<Position> capturedPositions) {
        NQueryRequest nQueryRequest = buildRetrieveOpenedPositionsRequest(capturedPositions);
        final ResponseEntity<SResponseNQueryResponsePositionOutDTO> response = positionSpireApiClient
            .getPositions(nQueryRequest);
        if (responseHasData(response)) {
            List<PositionOutDTO> updatedPositions = response.getBody().getData().getBeans();
            return updatedPositions.stream().map(backOfficeMapper::toModel).toList();
        }
        return List.of();
    }

    public List<Position> retrieveCancelledPositions(List<Position> persistedPositions) {
        NQueryRequest nQueryRequest = buildRetrieveCancelledPositionsRequest(persistedPositions);
        final ResponseEntity<SResponseNQueryResponsePositionOutDTO> response = positionSpireApiClient
            .getPositions(nQueryRequest);
        if (responseHasData(response)) {
            List<PositionOutDTO> updatedPositions = response.getBody().getData().getBeans();
            return updatedPositions.stream().map(backOfficeMapper::toModel).toList();
        }
        return List.of();
    }

    private NQueryRequest buildRetrieveOpenedPositionsRequest(List<Position> positionList) {
        NQuery nQuery = new NQuery().andOr(NQuery.AndOrEnum.AND).empty(true)
            .tuples(createTuplesGetPositionsByPositionStatus(positionList, OPEN));
        return new NQueryRequest().nQuery(nQuery);
    }

    private NQueryRequest buildRetrieveCancelledPositionsRequest(List<Position> positionList) {
        NQuery nQuery = new NQuery().andOr(NQuery.AndOrEnum.AND).empty(true)
            .tuples(createTuplesGetPositionsByPositionStatus(positionList, CANCELLED));
        return new NQueryRequest().nQuery(nQuery);
    }

    private List<NQueryTuple> createTuplesGetPositionsByPositionStatus(List<Position> positionList,
        PositionStatusEnum positionStatus) {
        String idListSequence = positionList.stream()
            .map(position -> String.valueOf(position.getPositionId()))
            .collect(Collectors.joining(","));
        return List.of(
            buildTuple(POSITION_ID, IN, idListSequence),
            buildTuple(STATUS, EQUALS, positionStatus.getValue())
        );
    }

    public List<RerateTrade> getNewBackOfficeRerateTradeEvents(Optional<Long> lastTradeId) {
        Long maxTradeId = lastTradeId.orElse(STARTING_TRADE_ID);
        NQuery nQuery = new NQuery().andOr(NQuery.AndOrEnum.AND)
            .tuples(createTuplesGetNewTrades(maxTradeId.toString()));
        NQueryRequest nQueryRequest = new NQueryRequest().nQuery(nQuery);
        ResponseEntity<SResponseNQueryResponseTradeOutDTO> response = tradeSpireApiClient.getTrades(nQueryRequest);
        if (response.getBody() != null) {
            if (StatusEnum.ERROR.equals(response.getBody().getStatus())) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, response.getBody().getMessage());
            }
            if (response.getBody().getData() != null && response.getBody().getData().getTotalRows() != null
                && response.getBody().getData().getTotalRows() > 0) {
                List<TradeOutDTO> tradeOutDTOList = response.getBody().getData().getBeans();
                return tradeOutDTOList.stream().map(this::mapBackOfficeTradeOutDTOToRerateTrade)
                    .collect(Collectors.toList());
            }
        }
        return List.of();
    }

    public List<ReturnTrade> retrieveNewReturnTrades(Optional<Long> lastTradeId) {
        Long maxTradeId = lastTradeId.orElse(STARTING_TRADE_ID);
        NQuery nQuery = new NQuery().andOr(NQuery.AndOrEnum.AND)
            .tuples(createTuplesGetReturnTrades(maxTradeId.toString()));
        NQueryRequest nQueryRequest = new NQueryRequest().nQuery(nQuery);
        ResponseEntity<SResponseNQueryResponseTradeOutDTO> response = tradeSpireApiClient.getTrades(nQueryRequest);
        if (response.getBody().getData() != null
            && response.getBody().getData().getTotalRows() != null
            && response.getBody().getData().getTotalRows() > 0) {
            List<TradeOutDTO> tradeOutDTOList = response.getBody().getData().getBeans();
            return tradeOutDTOList.stream().map(this::mapBackOfficeTradeOutDTOToReturnTrade)
                .collect(Collectors.toList());
        }
        return List.of();
    }

    public List<ReturnTrade> retrieveOpenReturnTrades(List<Long> tradeIds) {
        NQuery nQuery = new NQuery().andOr(NQuery.AndOrEnum.AND)
            .tuples(createTuplesGetOpenReturnTrades(tradeIds));
        NQueryRequest nQueryRequest = new NQueryRequest().nQuery(nQuery).start(0L);
        ResponseEntity<SResponseNQueryResponseTradeOutDTO> response = tradeSpireApiClient.getTrades(nQueryRequest);
        if (response.getBody().getData() != null
            && response.getBody().getData().getTotalRows() != null
            && response.getBody().getData().getTotalRows() > 0) {
            List<TradeOutDTO> tradeOutDTOList = response.getBody().getData().getBeans();
            return tradeOutDTOList.stream().map(this::mapBackOfficeTradeOutDTOToReturnTrade)
                .collect(Collectors.toList());
        }
        return List.of();
    }

    private List<NQueryTuple> createTuplesGetOpenReturnTrades(List<Long> tradeIds) {
        List<NQueryTuple> tuples = new ArrayList<>();
        tuples.add(
            new NQueryTuple().lValue("tradeId").operator(OperatorEnum.IN).rValue1(StringUtils.join(tradeIds, ",")));
        tuples.add(new NQueryTuple().lValue("status").operator(OperatorEnum.EQUALS).rValue1("OPEN"));
        return tuples;
    }

    public void confirmReturnTrade(ReturnTrade returnTrade) {
        OneSourceConfimationDTO body = new OneSourceConfimationDTO();
        body.setTradeId(returnTrade.getTradeId());
        body.setPositionId(returnTrade.getRelatedPositionId());
        body.setLedgerId(returnTrade.getMatching1SourceReturnId());
        body.userId(userId);
        body.setUserName(userName);
        tradeSpireApiClient.confirmTrade(body);
    }

    public void confirmBackOfficeRerateTrade(RerateTrade rerateTrade) {
        OneSourceConfimationDTO body = new OneSourceConfimationDTO();
        body.setTradeId(rerateTrade.getTradeId());
        body.setPositionId(rerateTrade.getRelatedPositionId());
        body.setLedgerId(rerateTrade.getMatchingRerateId());
        body.userId(userId);
        body.setUserName(userName);
        tradeSpireApiClient.confirmTrade(body);
    }

    public Optional<Settlement> retrieveSettlementInstruction(Position position,
        PartyRole partyRole, Long accountId) throws InstructionRetrievementException {
        NQuery nQuery = new NQuery().andOr(NQuery.AndOrEnum.AND)
            .tuples(createListOfTuplesGetInstruction(String.valueOf(position.getExposure().getDepoId()),
                String.valueOf(position.getPositionSecurityId()),
                String.valueOf(position.getPositionType().getPositionTypeId()),
                String.valueOf(position.getCurrencyId()), String.valueOf(accountId)));
        NQueryRequest nQueryRequest = new NQueryRequest().nQuery(nQuery);
        try {
            final ResponseEntity<SResponseNQueryResponseInstructionDTO> response = instructionClient
                .getSettlementDetails(nQueryRequest);
            if (responseHasInstruction(response)) {
                final List<InstructionDTO> instructionList = response.getBody().getData().getBeans();
                log.debug("Settlement Instructions for {} retrieved, size: {}.", partyRole, instructionList.size());
                return convertToSettlement(partyRole, instructionList);
            }
            return Optional.empty();
        } catch (RestClientException e) {
            throw new InstructionRetrievementException(e);
        }
    }

    public void updateSettlementInstruction(@NonNull Settlement settlement) {
        InstructionDTO updatedInstruction = createInstructionUpdateRequestBody(settlement);
        instructionClient.updateInstruction(updatedInstruction);
    }

    private InstructionDTO createInstructionUpdateRequestBody(Settlement settlement) {
        try {
            final AccountDTO accountDTO = new AccountDTO();
            accountDTO.setDtc(
                Long.valueOf(settlement.getInstruction().getDtcParticipantNumber()));
            final SwiftbicDTO swiftBic = new SwiftbicDTO();
            swiftBic.setBic(settlement.getInstruction().getSettlementBic());
            swiftBic.setBranch(settlement.getInstruction().getLocalAgentBic());

            return createInstruction(settlement, accountDTO, swiftBic);
        } catch (NumberFormatException e) {
            log.warn("Parse data exception. Check the data correctness");
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }

    private static InstructionDTO createInstruction(Settlement settlement, AccountDTO accountDTO,
        SwiftbicDTO swiftBic) {
        InstructionDTO instruction = new InstructionDTO();
        instruction.setAgentName(settlement.getInstruction().getLocalAgentName());
        instruction.setAgentSafe(settlement.getInstruction().getLocalAgentAcct());
        instruction.setAccountDTO(accountDTO);
        instruction.setAgentBicDTO(swiftBic);
        return instruction;
    }

    public Boolean update1SourceLoanContractIdentifier(Position position) {
        PositionDTO positionDTO = new PositionDTO().positionReferenceNumber(
            position.getMatching1SourceLoanContractId());
        TradeDTO originalTrade = new TradeDTO().positionId(position.getPositionId()).positionDTO(positionDTO);
        LoanTradeInputDTO loanTradeInputDTO = new LoanTradeInputDTO().originalTrade(originalTrade);
        try {
            ResponseEntity<SResponsePositionDTO> response = positionSpireApiClient.editPosition(loanTradeInputDTO);
            return response.getBody() != null && response.getBody().isSuccess();
        } catch (RestClientException e) {
            if (e instanceof HttpStatusCodeException exception) {
                final HttpStatusCode statusCode = exception.getStatusCode();
                if (Set.of(CREATED, UNAUTHORIZED, FORBIDDEN, NOT_FOUND)
                    .contains(HttpStatus.valueOf(statusCode.value()))) {
                    log.warn("SPIRE error response for {} subprocess. Details: {}", POST_POSITION_UPDATE, statusCode);
                    recordPositionContractIdentifierUpdateExceptionEvent(position, exception);
                }
            }
        }
        return false;
    }

    private List<NQueryTuple> createTuplesGetPositionByTradeLink(String tradeLink) {
        return List.of(new NQueryTuple().lValue("customValue2").operator(EQUALS).rValue1(tradeLink));
    }

    private List<NQueryTuple> createTuplesGetNewTradePositions(String lastTradeId) {
        return List.of(
            buildTuple(TRADE_TYPE, IN, join(COMMA_DELIMITER, List.of(NEW_LOAN, NEW_BORROW))),
            buildTuple(TRADE_STATUS, EQUALS, PENDING_ONESOURCE_CONFIRMATION),
            buildTuple(TRADE_ID, GREATER_THAN, lastTradeId)
        );
    }

    private List<NQueryTuple> createTuplesGetUpdatePositionsByIdList(Long lastTradeId, String positionIdList) {
        List<String> typeList = List.of(RERATE, RERATE_BORROW, ROLL_LOAN, ROLL_BORROW, CANCEL_LOAN, CANCEL_NEW_BORROW);
        return List.of(
            buildTuple(TRADE_TYPE, IN, join(COMMA_DELIMITER, typeList)),
            buildTuple(TRADE_STATUS, EQUALS, OPEN.getValue()),
            buildTuple(TRADE_ID, GREATER_THAN, String.valueOf(lastTradeId)),
            buildTuple(POSITION_ID, IN, positionIdList)
        );
    }

    private static NQueryTuple buildTuple(String lValue, OperatorEnum operator,
        String rValue1) {
        return new NQueryTuple()
            .lValue(lValue)
            .operator(operator)
            .rValue1(rValue1);
    }

    private List<NQueryTuple> createTuplesGetNewPositions(String positionId) {
        List<NQueryTuple> tuples = new ArrayList<>();
        tuples.add(
            new NQueryTuple().lValue("positionId").operator(NQueryTuple.OperatorEnum.GREATER_THAN).rValue1(positionId));
        tuples.add(new NQueryTuple().lValue("status").operator(IN).rValue1("FUTURE"));
        tuples.add(new NQueryTuple().lValue("positionType").operator(IN)
            .rValue1(join(",", positionTypes)));
        tuples.add(new NQueryTuple().lValue("depoKy").operator(IN).rValue1("DTC"));
        return tuples;
    }

    private List<NQueryTuple> createListOfTuplesGetInstruction(String depoId, String securityId, String positionTypeId,
        String currencyId, String accountId) {
        List<NQueryTuple> tuples = new ArrayList<>();
        tuples.add(new NQueryTuple().lValue("accountId").operator(EQUALS).rValue1(accountId));
        tuples.add(new NQueryTuple().lValue("depoId")
            .operator(EQUALS).rValue1(depoId));
        tuples.add(new NQueryTuple().lValue("securityId")
            .operator(EQUALS).rValue1(securityId));
        tuples.add(new NQueryTuple().lValue("positionTypeId")
            .operator(EQUALS).rValue1(positionTypeId));
        tuples.add(new NQueryTuple().lValue("currencyId")
            .operator(EQUALS).rValue1(currencyId));
        return tuples;
    }

    private boolean responseHasInstruction(ResponseEntity<SResponseNQueryResponseInstructionDTO> response) {
        return response.getBody() != null && response.getBody().getData() != null
            && response.getBody().getData().getTotalRows() > 0;
    }

    private Optional<Settlement> convertToSettlement(PartyRole partyRole, List<InstructionDTO> instructions) {
        if (instructions.isEmpty()) {
            return Optional.empty();
        }
        InstructionDTO instruction = instructions.get(0); // expected one instruction for the partyRole
        String bic = instruction.getBicDTO() == null ? null : instruction.getBicDTO().getBic();
        final SettlementInstruction settlementInstruction = buildSettlementInstruction(
            instruction, bic);
        Long instructionId = instruction.getInstructionId();
        Settlement settlement = Settlement.builder()
            .partyRole(partyRole)
            .instructionId(instructionId)
            .instruction(settlementInstruction).build();
        return Optional.of(settlement);
    }

    private SettlementInstruction buildSettlementInstruction(InstructionDTO instruction, String bic) {
        String agentName = instruction.getAgentName();
        String agentBic = instruction.getAgentBicDTO() == null ? null : instruction.getAgentBicDTO().getBic();
        String agentBranch = instruction.getAgentBicDTO() == null ? null : instruction.getAgentBicDTO().getBranch();
        String agentBicAndBranch = agentBic == null ? agentBranch : agentBic + agentBranch;
        String agentAcc = instruction.getAgentSafe();
        String dtc = instruction.getAccountDTO() == null ? null
            : String.valueOf(instruction.getAccountDTO().getDtc());
        SettlementInstruction settlementInstruction = new SettlementInstruction();
        settlementInstruction.setSettlementBic(bic);
        settlementInstruction.setLocalAgentName(agentName);
        settlementInstruction.setLocalAgentBic(agentBicAndBranch);
        settlementInstruction.setLocalAgentAcct(agentAcc);
        settlementInstruction.setDtcParticipantNumber(dtc);
        return settlementInstruction;
    }

    private List<NQueryTuple> createTuplesGetPositionsByCustomeValue2(String venueRefId) {
        List<NQueryTuple> tuples = new ArrayList<>();
        tuples.add(new NQueryTuple().lValue("customValue2").operator(EQUALS).rValue1(venueRefId));
        return tuples;
    }

    private List<NQueryTuple> createTuplesGetNewTrades(String maxTradeId) {
        List<NQueryTuple> tuples = new ArrayList<>();
        tuples.add(
            new NQueryTuple().lValue("tradeType").operator(NQueryTuple.OperatorEnum.IN)
                .rValue1("Rerate, Rerate Borrow"));
        tuples.add(new NQueryTuple().lValue("tradeId").operator(OperatorEnum.GREATER_THAN).rValue1(maxTradeId));
        tuples.add(new NQueryTuple().lValue("status").operator(OperatorEnum.EQUALS)
            .rValue1("PENDING ONESOURCE CONFIRMATION"));
        return tuples;
    }

    private RerateTrade mapBackOfficeTradeOutDTOToRerateTrade(TradeOutDTO tradeOutDTO) {
        TradeOut tradeOut = backOfficeMapper.toModel(tradeOutDTO);
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeOut(tradeOut);
        rerateTrade.setTradeId(tradeOut.getTradeId());
        rerateTrade.setRelatedPositionId(Long.valueOf(tradeOut.getPosition().getPositionId()));
        rerateTrade.setRelatedContractId(tradeOutDTO.getPositionOutDTO().getLedgerId());
        return rerateTrade;
    }

    private List<NQueryTuple> createTuplesGetReturnTrades(String maxTradeId) {
        List<NQueryTuple> tuples = new ArrayList<>();
        tuples.add(
            new NQueryTuple().lValue("tradeType").operator(NQueryTuple.OperatorEnum.IN)
                .rValue1("Return Loan, Return Borrow"));
        tuples.add(new NQueryTuple().lValue("tradeId").operator(OperatorEnum.GREATER_THAN).rValue1(maxTradeId));
        tuples.add(new NQueryTuple().lValue("status").operator(OperatorEnum.EQUALS)
            .rValue1("PENDING ONESOURCE CONFIRMATION"));
        return tuples;
    }

    private ReturnTrade mapBackOfficeTradeOutDTOToReturnTrade(TradeOutDTO tradeOutDTO) {
        TradeOut tradeOut = backOfficeMapper.toModel(tradeOutDTO);
        ReturnTrade returnTrade = new ReturnTrade();
        returnTrade.setTradeId(tradeOut.getTradeId());
        returnTrade.setTradeOut(tradeOut);
        returnTrade.setRelatedPositionId(tradeOut.getPosition().getPositionId());
        returnTrade.setRelatedContractId(tradeOutDTO.getPositionOutDTO().getLedgerId());
        return returnTrade;
    }

    private void recordPositionExceptionEvent(HttpStatusCodeException exception, IntegrationProcess process,
        IntegrationSubProcess subProcess) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(process);
        final CloudEventBuildRequest recordRequest = eventBuilder.buildExceptionRequest(exception, subProcess);
        cloudEventRecordService.record(recordRequest);
    }

    private void recordPositionContractIdentifierUpdateExceptionEvent(Position position,
        HttpStatusCodeException exception) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        final CloudEventBuildRequest recordRequest = eventBuilder.buildExceptionRequest(
            position.getMatching1SourceLoanContractId(), exception, IntegrationSubProcess.POST_POSITION_UPDATE,
            String.valueOf(position.getPositionId()));
        cloudEventRecordService.record(recordRequest);
    }

    public BackOfficeService(PositionSpireApiClient positionSpireApiClient, TradeSpireApiClient tradeSpireApiClient,
        InstructionSpireApiClient instructionClient, Integer userId, String userName,
        BackOfficeMapper backOfficeMapper, CloudEventRecordService cloudEventRecordService) {
        this.positionSpireApiClient = positionSpireApiClient;
        this.tradeSpireApiClient = tradeSpireApiClient;
        this.instructionClient = instructionClient;
        this.userId = userId;
        this.userName = userName;
        this.backOfficeMapper = backOfficeMapper;
        this.cloudEventRecordService = cloudEventRecordService;
    }

}
