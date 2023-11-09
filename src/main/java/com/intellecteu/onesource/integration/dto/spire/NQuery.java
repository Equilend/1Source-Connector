package com.intellecteu.onesource.integration.dto.spire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NQuery {

    @JsonProperty("andOr")
    private AndOr andOr;
    @JsonProperty("empty")
    private Boolean empty;
    @JsonProperty("queries")
    private List<String> queries;
    @JsonProperty("tuples")
    private List<Tuples> tuples;

    @JsonCreator
    public NQuery(@JsonProperty("andOr") AndOr andOr, @JsonProperty("empty") Boolean empty,
        @JsonProperty("queries") List<String> queries, @JsonProperty("tuples") List<Tuples> tuples) {
        this.andOr = andOr;
        this.empty = empty;
        this.queries = queries;
        this.tuples = tuples;
    }
}
