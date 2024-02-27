package com.intellecteu.onesource.integration.repository.entity.backoffice;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "position")
public class PositionEntity {

    @Id
    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "trade_id")
    private Long tradeId;

    @Column(name = "creation_datetime")
    private LocalDateTime creationDatetime;

    @Column(name = "venue_ref_id")
    private String venueRefId;

    @Column(name = "custom_value2")
    private String customValue2;

    @Column(name = "position_ref")
    private String positionRef;

    @Column(name = "rate")
    private Double rate;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "trade_date")
    private LocalDateTime tradeDate;

    @Column(name = "term_id")
    private Integer termId;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "settle_date")
    private LocalDateTime settleDate;

    @Column(name = "accrual_date")
    private LocalDateTime accrualDate;

    @Column(name = "deliver_free")
    private Boolean deliverFree;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "price")
    private Double price;

    @Column(name = "contract_value")
    private Double contractValue;

    @Column(name = "currency_id")
    private Integer currencyId;

    @Column(name = "position_security_id")
    private Long positionSecurityId;

    @Column(name = "processing_status")
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;

    @Column(name = "last_update_datetime")
    private LocalDateTime lastUpdateDateTime;

    @Column(name = "matching_1source_trade_agreement_id")
    private String matching1SourceTradeAgreementId;

    @Column(name = "matching_1source_loan_contract_id")
    private String matching1SourceLoanContractId;

    @Embedded
    @JsonProperty("securityDetailDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "bloombergId", column = @Column(name = "bloomberg_id")),
        @AttributeOverride(name = "quickCode", column = @Column(name = "quick_code")),
        @AttributeOverride(name = "priceFactor", column = @Column(name = "price_factor"))
    })
    private PositionSecurityDetailEntity positionSecurityDetail;

    @Embedded
    @JsonProperty("currencyDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "currencyKy", column = @Column(name = "currency"))
    })
    private CurrencyEntity currency;

    @Embedded
    @JsonProperty("loanBorrowDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "taxWithholdingRate", column = @Column(name = "tax_with_holding_rate"))
    })
    private LoanBorrowEntity loanBorrow;

    @Embedded
    @JsonProperty("collateralTypeDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "collateralType", column = @Column(name = "collateral_type"))
    })
    private PositionCollateralTypeEntity positionCollateralType;

    @Embedded
    @JsonProperty("exposureDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "cpHaircut", column = @Column(name = "cp_haircut")),
        @AttributeOverride(name = "cpMarkRoundTo", column = @Column(name = "cp_mark_round_to")),
        @AttributeOverride(name = "depoId", column = @Column(name = "depo_id"))
    })
    private PositionExposureEntity exposure;

    @Embedded
    @JsonProperty("positiontypeDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "positionTypeId", column = @Column(name = "position_type_id")),
        @AttributeOverride(name = "positionType", column = @Column(name = "position_type"))
    })
    private PositionTypeEntity positionType;

    @Embedded
    @JsonProperty("indexDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "indexId", column = @Column(name = "index_id")),
        @AttributeOverride(name = "indexName", column = @Column(name = "index_name"))
    })
    private IndexEntity index;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {PERSIST, MERGE, REFRESH})
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @ToString.Exclude
    private PositionAccountEntity account;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {PERSIST, MERGE, REFRESH})
    @JoinColumn(name = "cp_id", referencedColumnName = "id")
    @ToString.Exclude
    private PositionAccountEntity counterParty;

    @Embedded
    @JsonProperty("statusDTO")
    private PositionStatusEntity positionStatus;

    public void setAccount(PositionAccountEntity account) {
        this.account = account;
        account.getPositionsAccount().add(this);
    }

    public void setCounterParty(PositionAccountEntity counterParty) {
        this.counterParty = counterParty;
        counterParty.getPositionsAccount().add(this);
    }
}
