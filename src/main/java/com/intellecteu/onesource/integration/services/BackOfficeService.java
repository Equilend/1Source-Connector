package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.constant.PositionConstant.BORROWER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.constant.PositionConstant.LENDER_POSITION_TYPE;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.CONTRACT_INITIATION;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.GENERIC;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_TRADE_EVENTS_PENDING_CONFIRMATION;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.POST_POSITION_UPDATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.mapper.RerateTradeMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.spire.Position;
import com.intellecteu.onesource.integration.model.spire.RerateTrade;
import com.intellecteu.onesource.integration.model.spire.TradeOut;
import com.intellecteu.onesource.integration.services.client.spire.PositionSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.TradeSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.dto.LoanTradeInputDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQuery;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryRequest;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryTuple;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryTuple.OperatorEnum;
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponsePositionOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponseTradeOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponsePositionDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.TradeDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.TradeOutDTO;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    private final SpireMapper spireMapper;
    private final RerateTradeMapper rerateTradeMapper;
    private CloudEventRecordService cloudEventRecordService;

    public BackOfficeService(PositionSpireApiClient positionSpireApiClient, TradeSpireApiClient tradeSpireApiClient,
        SpireMapper spireMapper, RerateTradeMapper rerateTradeMapper, CloudEventRecordService cloudEventRecordService) {
        this.positionSpireApiClient = positionSpireApiClient;
        this.tradeSpireApiClient = tradeSpireApiClient;
        this.spireMapper = spireMapper;
        this.rerateTradeMapper = rerateTradeMapper;
        this.cloudEventRecordService = cloudEventRecordService;
    }

    public List<Position> getNewSpirePositions(Optional<String> lastPositionId) {
        String maxPositionId = lastPositionId.orElse(STARTING_POSITION_ID);
        NQuery nQuery = new NQuery().andOr(NQuery.AndOrEnum.AND)
            .tuples(createTuplesGetNewPositions(maxPositionId));
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
                    log.warn("SPIRE error response for {} subprocess. Details: {}",
                        GET_NEW_POSITIONS_PENDING_CONFIRMATION, exception.getStatusCode());
                    recordNewPositionExceptionEvent(exception);
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

    private static List<NQueryTuple> createTuplesGetNewPositions(String positionId) {
        List<NQueryTuple> tuples = new ArrayList<>();
        tuples.add(
            new NQueryTuple().lValue("positionId").operator(NQueryTuple.OperatorEnum.GREATER_THAN).rValue1(positionId));
        tuples.add(new NQueryTuple().lValue("status").operator(NQueryTuple.OperatorEnum.IN).rValue1("FUTURE"));
        tuples.add(new NQueryTuple().lValue("positionType").operator(NQueryTuple.OperatorEnum.IN)
            .rValue1(String.join(",", positionTypes)));
        tuples.add(new NQueryTuple().lValue("depoKy").operator(NQueryTuple.OperatorEnum.IN).rValue1("DTC"));
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

    public RerateTrade mapBackOfficeTradeOutDTOToRerateTrade(TradeOutDTO tradeOutDTO) {
        TradeOut tradeOut = rerateTradeMapper.toModel(tradeOutDTO);
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeOut(tradeOut);
        rerateTrade.setTradeId(tradeOut.getTradeId());
        rerateTrade.setRelatedPositionId(Long.valueOf(tradeOut.getPosition().getPositionId()));
        return rerateTrade;
    }


    private void recordNewPositionExceptionEvent(HttpStatusCodeException exception) {
        var eventBuilder = cloudEventRecordService.getFactory().eventBuilder(CONTRACT_INITIATION);
        final CloudEventBuildRequest recordRequest = eventBuilder.buildExceptionRequest(exception,
            IntegrationSubProcess.GET_NEW_POSITIONS_PENDING_CONFIRMATION);
        cloudEventRecordService.record(recordRequest);
    }

    private void recordPositionContractIdentifierUpdateExceptionEvent(Position position,
        HttpStatusCodeException exception) {
        var eventBuilder = cloudEventRecordService.getFactory()
            .eventBuilder(CONTRACT_INITIATION);
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

}
