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
public class RebateRate {

    @JsonIgnore
    private Long id;
    private FixedRate fixed;
    private FloatingRate floating;

    public Double retrieveBaseRate() {
        if (fixed == null && floating == null) {
            return null;
        }
        return fixed == null ? floating.getEffectiveRate() : fixed.getBaseRate();
    }

}
