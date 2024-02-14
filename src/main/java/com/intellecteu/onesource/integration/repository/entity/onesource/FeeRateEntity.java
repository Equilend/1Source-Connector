package com.intellecteu.onesource.integration.repository.entity.onesource;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "fee")
public class FeeRateEntity {

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
