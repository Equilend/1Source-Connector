package com.intellecteu.onesource.integration.model.spire;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Embeddable;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionSecurityDetail {

    private String ticker;
    private String cusip;
    private String isin;
    private String sedol;
    private String quickCode;
    private String bloombergId;

}
