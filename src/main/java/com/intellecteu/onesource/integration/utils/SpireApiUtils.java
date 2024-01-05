package com.intellecteu.onesource.integration.utils;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.intellecteu.onesource.integration.dto.spire.AndOr;
import com.intellecteu.onesource.integration.dto.spire.NQuery;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.dto.spire.Query;
import com.intellecteu.onesource.integration.dto.spire.Tuples;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@UtilityClass
@Slf4j
public class SpireApiUtils {

    public static HttpHeaders getDefaultHttpHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        return headers;
    }

    public static NQuery createGetInstructionsNQuery(Integer accountId, Long securityId,
        Integer positionTypeId, Integer currencyId) {
        return NQuery.builder()
            .queries(null)
            .andOr(AndOr.AND)
            .empty(true)
            .tuples(createListOfTuplesGetInstruction(accountId, securityId, positionTypeId, currencyId))
            .build();
    }


    public static NQuery createCpGetInstructionsNQuery(PositionDto position, String accountId) {
        return NQuery.builder()
            .queries(null)
            .andOr(AndOr.AND)
            .empty(true)
            .tuples(createTuplesGetInstruction(position, accountId))
            .build();
    }

    public static NQuery createGetPositionNQuery(List<String> queries, AndOr andOr, Boolean empty,
        List<Tuples> tuples) {
        return NQuery.builder()
            .queries(queries)
            .andOr(andOr)
            .empty(empty)
            .tuples(tuples)
            .build();
    }

    public static List<Tuples> createListOfTuplesGetPosition(String lValue, String operator, String rValue1,
        String rValue2) {
        List<Tuples> tuples = new ArrayList<>();
        tuples.add(createTuples(lValue, operator, rValue1, rValue2));

        return tuples;
    }

    public static List<Tuples> createListOfTuplesGetPositionWithoutTA(String positionId) {
        List<Tuples> tuples = new ArrayList<>();
        tuples.add(createTuples("positionId", "GREATER_THAN", positionId, null));
        tuples.add(createTuples("status", "IN", "FUTURE", null));
        tuples.add(createTuples("positionType", "IN", "CASH LOAN,CASH BORROW", null));
        tuples.add(createTuples("depoKy", "IN", "DTC", null));

        return tuples;
    }

    public static List<Tuples> createListOfTuplesUpdatedPositions(String dateTime, String ids) {
        List<Tuples> tuples = new ArrayList<>();
        tuples.add(createTuples("positionId", "IN", ids, null));
        tuples.add(createTuples("lastModTs", "GREATER THAN", dateTime, null));

        return tuples;
    }

    public static List<Tuples> createListOfTuplesGetInstruction(Integer accountId, Long securityId,
        Integer positionTypeId, Integer currencyId) {
        List<Tuples> tuples = new ArrayList<>();
        tuples.add(createTuples("accountId", "EQUALS", String.valueOf(accountId), null));
        tuples.add(createTuples("securityId", "EQUALS", String.valueOf(securityId), null));
        tuples.add(createTuples("positionTypeId", "EQUALS", String.valueOf(positionTypeId), null));
        tuples.add(createTuples("currencyId", "EQUALS", String.valueOf(currencyId), null));
        return tuples;
    }

    private static List<Tuples> createTuplesGetInstruction(PositionDto position, String accountId) {
        List<Tuples> tuples = new ArrayList<>();
        tuples.add(createTuples("accountId", "EQUALS", accountId, null));
        tuples.add(createTuples("depoId", "EQUALS", String.valueOf(position.getDepoId()), null));
        tuples.add(createTuples("securityId", "EQUALS", String.valueOf(position.getSecurityId()), null));
        tuples.add(createTuples("positionTypeId", "EQUALS", String.valueOf(position.getPositionTypeId()), null));
        tuples.add(createTuples("currencyId", "EQUALS", String.valueOf(position.getCurrencyId()), null));
        return tuples;
    }


    public static Tuples createTuples(String lValue, String operator, String rValue1, String rValue2) {
        return Tuples.builder()
            .lValue(lValue)
            .operator(operator)
            .rValue1(rValue1)
            .rValue2(rValue2)
            .build();
    }

    public static HttpEntity<Query> buildRequest(NQuery nQuery) {
        Query query = Query.builder().nQuery(nQuery).build();

        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        return new HttpEntity<>(query, headers);
    }
}
