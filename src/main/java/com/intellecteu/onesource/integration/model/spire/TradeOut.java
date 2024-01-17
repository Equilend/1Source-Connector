package com.intellecteu.onesource.integration.model.spire;

import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TradeOut {

    @Id
    private Long tradeId;
    private LocalDateTime tradeDate;
    private LocalDateTime postDate;
    private LocalDateTime settleDate;
    private String tradeType;
    private Integer tradeTypeId;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Position position;
}
