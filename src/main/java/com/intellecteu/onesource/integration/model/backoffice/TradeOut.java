package com.intellecteu.onesource.integration.model.backoffice;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeOut {

    private Long tradeId;
    private LocalDateTime tradeDate;
    private LocalDateTime postDate;
    private LocalDateTime settleDate;
    private LocalDateTime accrualDate;
    private Double rateOrSpread;
    private String tradeType;
    private Integer tradeTypeId;
    private Index index;
    private String status;
    private Integer statusId;
    private Position position;
    private Double quantity;
    private Double amount;
    private Account account;
    private Account counterParty;
    private Integer depoId;
    private String depoKy;
}
