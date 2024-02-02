package com.intellecteu.onesource.integration.repository.entity.backoffice;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PositionSecurityDetailEntity {

    private String ticker;
    private String cusip;
    private String isin;
    private String sedol;
    private String quickCode;
    private String bloombergId;
    private String description;
    private Integer priceFactor;
    private Double baseRebateRate;

}
