package com.intellecteu.onesource.integration.model.backoffice;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionSecurityDetail {

    private Integer securityId;
    private String ticker;
    private String cusip;
    private String isin;
    private String sedol;
    private String quickCode;
    private String bloombergId;
    private String description;
    private Double securityPrice;
    private Integer priceFactor;

}
