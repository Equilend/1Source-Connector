package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class FeeRate {

    private Long id;
    private Double baseRate;
    private Double effectiveRate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveDate;
    private String cutoffTime;

}
