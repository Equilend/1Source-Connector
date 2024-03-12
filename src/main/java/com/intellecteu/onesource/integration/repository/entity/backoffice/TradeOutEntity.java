package com.intellecteu.onesource.integration.repository.entity.backoffice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.backoffice.Index;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
    private LocalDateTime accrualDate;
    private Double rateOrSpread;
    private String tradeType;
    private Integer tradeTypeId;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "indexId", column = @Column(name = "index_id")),
        @AttributeOverride(name = "indexName", column = @Column(name = "index_name")),
        @AttributeOverride(name = "spread", column = @Column(name = "index_spread")),
    })
    private IndexEntity index;
    private String status;
    private Integer statusId;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private PositionEntity position;
}
