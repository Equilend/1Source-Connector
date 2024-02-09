package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloatingRate {

    @JsonIgnore
    private Long id;
    private Benchmark benchmark;
    private Double baseRate;
    private Double spread;
    private Double effectiveRate;
    private Boolean isAutoRerate;
    private Integer effectiveDateDelay;
    private LocalDate effectiveDate;
    private String cutoffTime;

}
