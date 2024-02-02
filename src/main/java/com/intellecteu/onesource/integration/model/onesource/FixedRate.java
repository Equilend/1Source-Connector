package com.intellecteu.onesource.integration.model.onesource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fixed")
public class FixedRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "base_rate")
    private Double baseRate;
    @Column(name = "effective_rate")
    private Double effectiveRate;
    @Column(name = "effective_date", columnDefinition = "DATE")
    private LocalDate effectiveDate;
    @Column(name = "cutoff_time")
    private String cutoffTime;
}
