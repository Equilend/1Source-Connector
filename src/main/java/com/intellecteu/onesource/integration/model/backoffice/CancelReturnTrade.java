package com.intellecteu.onesource.integration.model.backoffice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelReturnTrade {
    private Long tradeId;
    private Long offsetId;
    private String type;
}
