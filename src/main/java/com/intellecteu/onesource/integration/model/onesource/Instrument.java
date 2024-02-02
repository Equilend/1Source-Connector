package com.intellecteu.onesource.integration.model.onesource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instrument {

    private Long id;
    private String ticker;
    private String cusip;
    private String isin;
    private String sedol;
    private String quick;
    private String figi;
    private String description;
    private Price price;

}
