package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.constant.PositionConstant.BORROWER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.Field.COMMA_DELIMITER;
import static com.intellecteu.onesource.integration.constant.PositionConstant.LENDER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationProcess.GENERIC;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_TRADE_EVENTS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.GET_UPDATED_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess.POST_POSITION_UPDATE;
import static com.intellecteu.onesource.integration.utils.IntegrationUtils.formattedDateTime;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.exception.PositionRetrievementException;
import com.intellecteu.onesource.integration.exception.InstructionRetrievementException;
import com.intellecteu.onesource.integration.mapper.RerateTradeMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.model.onesource.SettlementInstruction;
import com.intellecteu.onesource.integration.model.backoffice.spire.Position;
import com.intellecteu.onesource.integration.model.backoffice.spire.RerateTrade;
import com.intellecteu.onesource.integration.model.backoffice.spire.TradeOut;
import com.intellecteu.onesource.integration.services.client.spire.InstructionSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.PositionSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.TradeSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.dto.AccountDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.LoanTradeInputDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQuery;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryRequest;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryTuple;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryTuple.OperatorEnum;
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponseInstructionDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponsePositionOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponseTradeOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponsePositionDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SwiftbicDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.TradeDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.TradeOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.instruction.InstructionDTO;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

//@Service initiated in @see com.intellecteu.onesource.integration.config.AppConfig
@Slf4j
public class BackOfficeService {

    private static final String STARTING_POSITION_ID = "0";
    private static final Long STARTING_TRADE_ID = 0l;
    private static final List<String> positionTypes = List.of(LENDER_POSITION_TYPE, BORROWER_POSITION_TYPE);

    private final PositionSpireApiClient positionSpireApiClient;
    private final TradeSpireApiClient tradeSpireApiClient;
    private final InstructionSpireApiClient instructionClient;
    private final SpireMapper spireMapper;
    private final RerateTradeMapper rerateTradeMapper;
    private CloudEventRecordService cloudEventRecordService;

    public List<Position> getNewSpirePositions(Optional<String> lastPositionId) {
        String maxPositionId = lastPositionId.orElse(STARTING_POSITION_ID);
        NQuery nQuery = new NQuery().andOr(NQuery.AndOrEnum.AND)
            .tuples(createTuplesGetNewPositions(maxPositionId));
        NQueryRequest nQueryRequest = new NQueryRequest().nQuery(nQuery);
        try {
            ResponseEntity<SResponseNQueryResponsePositionOutDTO> response = positionSpireApiClient.getPositions(
                nQueryRequest);
            if (responseHasData(response)) {
                List<PositionOutDTO> positionOutDTOList = response.getBody().getData().getBeans();
                return positionOutDTOList.stream().map(spireMapper::toPosition).collect(Collectors.toList());
            }
        } catch (RestClientException e) {
            if (e instanceof HttpStatusCodeException exception) {
                if (Set.of(CREATED, UNAUTHORIZED, FORBIDDEN).contains(exception.getStatusCode())) {
                    log.warn("SPIRE error response for {} subprocess. Details: {}",
                        GET_NEW_POSITIONS_PENDING_CONFIRMATION, exception.getStatusCode());
                    recordPositionExceptionEvent(exception, CONTRACT_INITIATION,
                        GET_NEW_POSITIONS_PENDING_CONFIRMATION);
                }
            }
        }
        return List.of();
    }

    public List<Position> getNewSpirePositions(LocalDateTime lastUpdate, List<Position> positionList) {
        String commaSeparatedIdList = positionList.stream()
            .map(Position::getPositionId)
            .collect(Collectors.joining(COMMA_DELIMITER));
        NQuery nQuery = new NQuery().andOr(NQuery.AndOrEnum.AND).tuples(
            createTuplesGetNewPositionsByIdList(formattedDateTime(lastUpdate), commaSeparatedIdList));
        NQueryRequest nQueryRequest = new NQueryRequest().nQuery(nQuery);
        try {
            ResponseEntity<SResponseNQueryResponsePositionOutDTO> response = positionSpireApiClient.getPositions(
                nQueryRequest);
            if (responseHasData(response)) {
                List<PositionOutDTO> positionOutDTOList = response.getBody().getData().getBeans();
                return positionOutDTOList.stream().map(spireMapper::toPosition).collect(Collectors.toList());
            }
        } catch (RestClientException e) {
            if (e instanceof HttpStatusCodeException exception) {
                log.warn("SPIRE error response for {} subprocess. Details: {}",
                    GET_UPDATED_POSITIONS_PENDING_CONFIRMATION, exception.getStatusCode());
                if (Set.of(ProcessingStatus.CREATED, UNAUTHORIZED, FORBIDDEN).contains(exception.getStatusCode())) {
                    recordPositionExceptionEvent(exception, CONTRACT_INITIATION,
                        GET_UPDATED_POSITIONS_PENDING_CONFIRMATION);
                }
            }
        }
        return List.of();
    }

    private List<NQueryTuple> createTuplesGetNewPositionsByIdList(String lastUpdate, String commaSeparatedIdList) {
        List<NQueryTuple> tuples = new ArrayList<>();
        tuples.add(new NQueryTuple().lValue("positionId").operator(OperatorEnum.IN).rValue1(commaSeparatedIdList));
        tuples.add(new NQueryTuple().lValue("lastModTs").operator(OperatorEnum.GREATER_THAN).rValue1(lastUpdate));
        return tuples;
    }

    private boolean responseHasData(ResponseEntity<SResponseNQueryResponsePositionOutDTO> response) {
        return response.getBody() != null && response.getBody().getData() != null
            && response.getBody().getData().getTotalRows() > 0;
    }

    public List<Position> getPositionByVenueRefId(String venueRefId) {
        NQuery nQuery = new NQuery().andOr(NQuery.AndOrEnum.AND).empty(true)
            .tuples(createTuplesGetPositionsByCustomeValue2(venueRefId));
        NQueryRequest nQueryRequest = new NQueryRequest().nQuery(nQuery);
        try {
            ResponseEntity<SResponseNQueryResponsePositionOutDTO> response = positionSpireApiClient.getPositions(
                nQueryRequest);
            if (response.getBody().getData() != null
                && response.getBody().getData().getTotalRows() > 0) {
                List<PositionOutDTO> positionOutDTOList = response.getBody().getData().getBeans();
                return positionOutDTOList.stream().map(spireMapper::toPosition).collect(Collectors.toList());
            }
        } catch (RestClientException e) {
            if (e instanceof HttpStatusCodeException exception) {
                if (Set.of(CREATED, UNAUTHORIZED, FORBIDDEN).contains(exception.getStatusCode())) {
                    log.error("SPIRE error response for getting position by venueRefId. Details: {}",
                        exception.getStatusCode());
                }
            }
        }
        return List.of();
    }

    public List<RerateTrade> getNewBackOfficeTradeEvents(Optional<Long> lastTradeId, List<String> positionIds) {
        Long maxTradeId = lastTradeId.orElse(STARTING_TRADE_ID);
        NQuery nQuery = new NQuery().andOr(NQuery.AndOrEnum.AND)
            .tuples(createTuplesGetNewTrades(maxTradeId.toString(), positionIds));
        NQueryRequest nQueryRequest = new NQueryRequest().nQuery(nQuery);
        try {
            ResponseEntity<SResponseNQueryResponseTradeOutDTO> response = tradeSpireApiClient.getTrades(nQueryRequest);
            if (response.getBody().getData() != null
                && response.getBody().getData().getTotalRows() > 0) {
                List<TradeOutDTO> tradeOutDTOList = response.getBody().getData().getBeans();
                return tradeOutDTOList.stream().map(this::mapBackOfficeTradeOutDTOToRerateTrade)
                    .collect(Collectors.toList());
            }
        } catch (RestClientException e) {
            if (e instanceof HttpStatusCodeException exception) {
                if (Set.of(CREATED, UNAUTHORIZED, FORBIDDEN).contains(exception.getStatusCode())) {
                    log.warn("SPIRE error response for {} subprocess. Details: {}",
                        GET_TRADE_EVENTS_PENDING_CONFIRMATION, exception.getStatusCode());
                    recordTradeEventExceptionEvent(exception);
                }
            }
        }
        return List.of();
    }

    public Optional<Settlement> retrieveSettlementInstruction(Position position,
        PartyRole partyRole, Long accountId) throws InstructionRetrievementException {
        NQuery nQuery = new NQuery().andOr(NQuery.AndOrEnum.AND)
            .tuples(createListOfTuplesGetInstruction(String.valueOf(position.getExposure().getDepoId()),
                String.valueOf(position.getSecurityId()), String.valueOf(position.getPositionTypeId()),
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
            final SwiftbicDTO swiftBic = new SwiftbicDTO(
                settlement.getInstruction().getSettlementBic(),
                settlement.getInstruction().getLocalAgentBic());

            return InstructionDTO.builder()
                .agentName(settlement.getInstruction().getLocalAgentName())
                .agentSafe(settlement.getInstruction().getLocalAgentAcct())
                .accountDTO(accountDTO)
                .agentBicDTO(swiftBic)
                .build();
        } catch (NumberFormatException e) {
            log.warn("Parse data exception. Check the data correctness");
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }

    public Optional<Position> getPositionForTrade(String venueRefId) {
        NQuery nquery = new NQuery().andOr(NQuery.AndOrEnum.AND).tuples(createTuplesGetPositionByTradeLink(venueRefId));
        NQueryRequest request = new NQueryRequest().nQuery(nquery);
        try {
            log.debug("Retrieving Spire Position by venueRefId={}", venueRefId);
            ResponseEntity<SResponseNQueryResponsePositionOutDTO> response = positionSpireApiClient
                .getPositions(request);
            if (responseHasData(response)) {
                if (response.getBody().getData().getTotalRows() > 1) {
                    log.warn("Multiple response found! Getting the first element");
                }
                var positionResponse = response.getBody().getData().getBeans().get(0);
                return Optional.of(spireMapper.toPosition(positionResponse));
            }
        } catch (RestClientException e) {
            log.warn("Unexpected exception on position retrievement by linked trade: {}", venueRefId);
            if (e instanceof HttpStatusCodeException exception) {
                throw new PositionRetrievementException(exception);
            }
        }
        return Optional.empty();
    }

    public Boolean update1SourceLoanContractIdentifier(Position position) {
        PositionDTO positionDTO = new PositionDTO().positionReferenceNumber(
            position.getMatching1SourceLoanContractId());
        TradeDTO originalTrade = new TradeDTO().positionId(Long.parseLong(position.getPositionId()))
            .positionDTO(positionDTO);
        LoanTradeInputDTO loanTradeInputDTO = new LoanTradeInputDTO().originalTrade(originalTrade);
        try {
            ResponseEntity<SResponsePositionDTO> response = positionSpireApiClient.editPosition(loanTradeInputDTO);
            return response.getBody() != null && response.getBody().isSuccess();
        } catch (RestClientException e) {
            if (e instanceof HttpStatusCodeException exception) {
                if (Set.of(CREATED, UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(exception.getStatusCode())) {
                    log.warn("SPIRE error response for {} subprocess. Details: {}",
                        POST_POSITION_UPDATE, exception.getStatusCode());
                    recordPositionContractIdentifierUpdateExceptionEvent(position, exception);
                }
            }
        }
        return false;
    }

    private List<NQueryTuple> createTuplesGetPositionByTradeLink(String tradeLink) {
        return List.of(new NQueryTuple().lValue("customValue2").operator(OperatorEnum.EQUALS).rValue1(tradeLink));
    }

    private List<NQueryTuple> createTuplesGetNewPositions(String positionId) {
        List<NQueryTuple> tuples = new ArrayList<>();
        tuples.add(
            new NQueryTuple().lValue("positionId").operator(NQueryTuple.OperatorEnum.GREATER_THAN).rValue1(positionId));
        tuples.add(new NQueryTuple().lValue("status").operator(NQueryTuple.OperatorEnum.IN).rValue1("FUTURE"));
        tuples.add(new NQueryTuple().lValue("positionType").operator(NQueryTuple.OperatorEnum.IN)
            .rValue1(String.join(",", positionTypes)));
        tuples.add(new NQueryTuple().lValue("depoKy").operator(NQueryTuple.OperatorEnum.IN).rValue1("DTC"));
        return tuples;
    }

    private List<NQueryTuple> createListOfTuplesGetInstruction(String depoId, String securityId, String positionTypeId,
        String currencyId, String accountId) {
        List<NQueryTuple> tuples = new ArrayList<>();
        tuples.add(new NQueryTuple().lValue("accountId").operator(OperatorEnum.EQUALS).rValue1(accountId));
        tuples.add(new NQueryTuple().lValue("depoId")
            .operator(OperatorEnum.EQUALS).rValue1(depoId));
        tuples.add(new NQueryTuple().lValue("securityId")
            .operator(OperatorEnum.EQUALS).rValue1(securityId));
        tuples.add(new NQueryTuple().lValue("positionTypeId")
            .operator(OperatorEnum.EQUALS).rValue1(positionTypeId));
        tuples.add(new NQueryTuple().lValue("currencyId")
            .operator(OperatorEnum.EQUALS).rValue1(currencyId));
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
        tuples.add(new NQueryTuple().lValue("customValue2").operator(OperatorEnum.EQUALS).rValue1(venueRefId));
        return tuples;
    }

    private List<NQueryTuple> createTuplesGetNewTrades(String maxTradeId, List<String> positionIds) {
        List<NQueryTuple> tuples = new ArrayList<>();
        tuples.add(
            new NQueryTuple().lValue("positionType").operator(NQueryTuple.OperatorEnum.IN)
                .rValue1(String.join(",", positionTypes)));
        tuples.add(new NQueryTuple().lValue("tradeId").operator(OperatorEnum.GREATER_THAN).rValue1(maxTradeId));
        tuples.add(new NQueryTuple().lValue("positionId").operator(NQueryTuple.OperatorEnum.IN)
            .rValue1(String.join(",", positionIds)));
        return tuples;
    }

    private RerateTrade mapBackOfficeTradeOutDTOToRerateTrade(TradeOutDTO tradeOutDTO) {
        TradeOut tradeOut = rerateTradeMapper.toModel(tradeOutDTO);
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeOut(tradeOut);
        rerateTrade.setTradeId(tradeOut.getTradeId());
        rerateTrade.setRelatedPositionId(Long.valueOf(tradeOut.getPosition().getPositionId()));
        return rerateTrade;
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
            position.getPositionId());
        cloudEventRecordService.record(recordRequest);
    }

    private void recordTradeEventExceptionEvent(HttpStatusCodeException exception) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(GENERIC);
        final CloudEventBuildRequest recordRequest = eventBuilder.buildExceptionRequest(exception,
            IntegrationSubProcess.GET_TRADE_EVENTS_PENDING_CONFIRMATION);
        cloudEventRecordService.record(recordRequest);
    }

    public BackOfficeService(PositionSpireApiClient positionSpireApiClient, TradeSpireApiClient tradeSpireApiClient,
        InstructionSpireApiClient instructionClient, SpireMapper spireMapper, RerateTradeMapper rerateTradeMapper,
        CloudEventRecordService cloudEventRecordService) {
        this.positionSpireApiClient = positionSpireApiClient;
        this.tradeSpireApiClient = tradeSpireApiClient;
        this.instructionClient = instructionClient;
        this.spireMapper = spireMapper;
        this.rerateTradeMapper = rerateTradeMapper;
        this.cloudEventRecordService = cloudEventRecordService;
    }

}
