package com.intellecteu.onesource.integration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "fee")
public class FeeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "base_rate")
    private Double baseRate;
    @Column(name = "effective_rate")
    private Double effectiveRate;
    @Column(name = "effective_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime effectiveDate;
    @Column(name = "cutoff_time")
    private String cutoffTime;
}
