package com.intellecteu.onesource.integration.model.onesource;

import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fixed")
public class FixedRate {

    public static final String FIXED_INDEX_NAME = "Fixed Rate";
    private Long id;
    private Double baseRate;
    private Double effectiveRate;
    private LocalDate effectiveDate;
    private String cutoffTime;

    public FixedRate(Double baseRate) {
        this.baseRate = baseRate;
    }
}
