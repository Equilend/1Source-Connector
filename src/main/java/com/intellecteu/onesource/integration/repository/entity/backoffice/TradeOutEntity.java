package com.intellecteu.onesource.integration.repository.entity.backoffice;

import jakarta.persistence.Table;
import java.time.LocalDateTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "trade_out")
public class TradeOutEntity {

    @Id
    private Long tradeId;
    private LocalDateTime tradeDate;
    private LocalDateTime postDate;
    private LocalDateTime settleDate;
    private String tradeType;
    private Integer tradeTypeId;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private PositionEntity position;
}
