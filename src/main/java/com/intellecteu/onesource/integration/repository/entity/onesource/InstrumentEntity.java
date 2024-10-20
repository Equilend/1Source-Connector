package com.intellecteu.onesource.integration.repository.entity.onesource;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "instrument")
public class InstrumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "security_id")
    private Long securityId;
    @Column(name = "ticker")
    private String ticker;
    @Column(name = "cusip")
    private String cusip;
    @Column(name = "isin")
    private String isin;
    @Column(name = "sedol")
    private String sedol;
    @Column(name = "quick_code")
    private String quick;
    @Column(name = "figi")
    private String figi;
    @Column(name = "description")
    private String description;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "price_id")
    private PriceEntity price;
}
