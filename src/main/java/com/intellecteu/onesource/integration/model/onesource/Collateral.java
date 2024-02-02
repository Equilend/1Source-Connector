package com.intellecteu.onesource.integration.model.onesource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "collateral")
public class Collateral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "contract_price")
    private Double contractPrice;
    @Column(name = "contract_value")
    private Double contractValue;
    @Column(name = "collateral_value")
    private Double collateralValue;
    @Column(name = "currency")
    private String currency;
    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private CollateralType type;
    @Column(name = "description")
    @Enumerated(value = EnumType.STRING)
    private CollateralDescription description;
    @Column(name = "margin")
    private Double margin;
    @Column(name = "rounding_rule")
    private Integer roundingRule;
    @Column(name = "rounding_mode")
    @Enumerated(value = EnumType.STRING)
    private RoundingMode roundingMode;
}
