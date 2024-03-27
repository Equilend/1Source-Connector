package com.intellecteu.onesource.integration.model.backoffice;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
//@NoArgsConstructor
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

    public TradeOut() {
        setIndexForDemo();
    }

    public void setIndex(Index index) {
        setIndexForDemo();
    }

    //TODO hardcode for the demo. Expected to be removed
    private void setIndexForDemo() {
        index = new Index();
        index.setIndexId(12);
        index.setIndexName("Fixed Rate");
    }
}
