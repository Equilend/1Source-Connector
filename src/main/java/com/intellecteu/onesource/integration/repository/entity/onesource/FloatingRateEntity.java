package com.intellecteu.onesource.integration.repository.entity.onesource;

import com.intellecteu.onesource.integration.model.onesource.Benchmark;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "floating")
public class FloatingRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "benchmark")
    @Enumerated(value = EnumType.STRING)
    private Benchmark benchmark;
    @Column(name = "base_rate")
    private Double baseRate;
    @Column(name = "spread")
    private Double spread;
    @Column(name = "effective_rate")
    private Double effectiveRate;
    @Column(name = "is_auto_rerate")
    private Boolean isAutoRerate;
    @Column(name = "effective_date_delay")
    private Integer effectiveDateDelay;
    @Column(name = "effective_date", columnDefinition = "DATE")
    private LocalDate effectiveDate;
    @Column(name = "cutoff_time")
    private String cutoffTime;
}
