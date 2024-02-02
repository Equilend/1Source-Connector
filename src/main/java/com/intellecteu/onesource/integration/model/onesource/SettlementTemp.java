package com.intellecteu.onesource.integration.model.onesource;

import java.util.List;
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
public class SettlementTemp {

    private Long id;
    private String contractId;
    private List<Settlement> settlement;

}
