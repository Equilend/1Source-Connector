package com.intellecteu.onesource.integration.utils;

import com.intellecteu.onesource.integration.dto.spire.AndOr;
import com.intellecteu.onesource.integration.dto.spire.NQuery;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.dto.spire.Tuples;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class SpireApiUtils {

    public static NQuery createGetInstructionsNQuery(PositionDto position) {
        return NQuery.builder()
            .queries(null)
            .andOr(AndOr.AND)
            .empty(true)
            .tuples(createListOfTuplesGetInstruction(position))
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
        tuples.add(createTuples("status", "IN", "FUTURE", null));
        tuples.add(createTuples("positionType", "IN", "CASH LOAN,CASH BORROW", null));
        tuples.add(createTuples("depoKy", "IN", "DTC", null));
        tuples.add(createTuples("positionId", "GREATER_THAN", positionId, null));

        return tuples;
    }

    public static List<Tuples> createListOfTuplesUpdatedPositions(String dateTime, String ids) {
        List<Tuples> tuples = new ArrayList<>();
        tuples.add(createTuples("positionId", "IN", ids, null));
        tuples.add(createTuples("lastModTs", "GREATER THAN", dateTime, null));

        return tuples;
    }

    public static List<Tuples> createListOfTuplesGetInstruction(PositionDto position) {
        List<Tuples> tuples = new ArrayList<>();
        tuples.add(createTuples("accountId", "EQUALS", String.valueOf(position.getDepoId()), null));
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
}
