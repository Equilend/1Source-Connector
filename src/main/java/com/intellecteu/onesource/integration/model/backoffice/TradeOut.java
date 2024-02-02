package com.intellecteu.onesource.integration.model.backoffice;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeOut {

    private Long tradeId;
    private LocalDateTime tradeDate;
    private LocalDateTime postDate;
    private LocalDateTime settleDate;
    private String tradeType;
    private Integer tradeTypeId;
    private Position position;

}
