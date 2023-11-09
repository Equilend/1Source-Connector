package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeRateDto {

    @JsonProperty("baseRate")
    private Double baseRate;
    @JsonProperty("effectiveRate")
    private Double effectiveRate;
    @JsonProperty("effectiveDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime effectiveDate;
    @JsonProperty("cutoffTime")
    private String cutoffTime;
}
