package com.intellecteu.onesource.integration.model.onesource;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Price {

    @JsonIgnore
    private Long id;
    private Double value;
    private String currency;
    private PriceUnit unit;

    public Price(PriceUnit unit) {
        this.unit = unit;
    }
}
