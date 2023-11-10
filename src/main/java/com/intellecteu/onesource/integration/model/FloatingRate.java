package com.intellecteu.onesource.integration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "floating")
public class FloatingRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
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
