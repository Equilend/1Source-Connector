package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.Benchmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloatingRateDto {

    @JsonProperty("benchmark")
    private Benchmark benchmark;
    @JsonProperty("baseRate")
    private Double baseRate;
    @JsonProperty("spread")
    private Double spread;
    @JsonProperty("effectiveRate")
    private Double effectiveRate;
    @JsonProperty("isAutoRerate")
    private Boolean isAutoRerate;
    @JsonProperty("effectiveDateDelay")
    private Integer effectiveDateDelay;
    @JsonProperty("effectiveDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveDate;
    @JsonProperty("cutoffTime")
    private String cutoffTime;
}
