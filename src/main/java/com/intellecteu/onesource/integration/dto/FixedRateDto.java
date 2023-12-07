package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixedRateDto {

    @JsonProperty("baseRate")
    private Double baseRate;
    @JsonProperty("effectiveRate")
    private Double effectiveRate;
    @JsonProperty("effectiveDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveDate;
    @JsonProperty("cutoffTime")
    private String cutoffTime;
}
