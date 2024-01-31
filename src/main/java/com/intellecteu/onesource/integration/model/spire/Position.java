package com.intellecteu.onesource.integration.model.spire;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.ProcessingStatus;
import java.time.LocalDateTime;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "position")
public class Position {

    @Id
    @Column(name = "spire_position_id")
    private String positionId;

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

    @Column(name = "security_id")
    private Long securityId;

    @Column(name = "position_type_id")
    private Integer positionTypeId;

    @Column(name = "processing_status")
    @Enumerated(value = EnumType.STRING)
    private ProcessingStatus processingStatus;

    @Column(name = "last_update_datetime")
    private LocalDateTime lastUpdateDateTime;

    @Column(name = "matching_1source_trade_agreement_id")
    private String matching1SourceTradeAgreementId;

    @Column(name = "matching_1source_loan_contract_id")
    private String matching1SourceLoanContractId;

    @Column(name = "applicable_instruction_id")
    private Long applicableInstructionId;

    @Embedded
    @JsonProperty("securityDetailDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "bloombergId", column = @Column(name = "bloomberg_id")),
        @AttributeOverride(name = "quickCode", column = @Column(name = "quick_code")),
        @AttributeOverride(name = "priceFactor", column = @Column(name = "price_factor")),
        @AttributeOverride(name = "baseRebateRate", column = @Column(name = "base_rebate_rate"))
    })
    private PositionSecurityDetail positionSecurityDetail;

    @Embedded
    @JsonProperty("currencyDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "currencyKy", column = @Column(name = "currency"))
    })
    private Currency currency;

    @Embedded
    @JsonProperty("loanBorrowDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "taxWithholdingRate", column = @Column(name = "tax_with_holding_rate"))
    })
    private LoanBorrow loanBorrow;

    @Embedded
    @JsonProperty("collateralTypeDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "collateralType", column = @Column(name = "collateral_type"))
    })
    private PositionCollateralType positionCollateralType;

    @Embedded
    @JsonProperty("exposureDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "cpHaircut", column = @Column(name = "cp_haircut")),
        @AttributeOverride(name = "cpMarkRoundTo", column = @Column(name = "cp_mark_round_to")),
        @AttributeOverride(name = "depoId", column = @Column(name = "depo_id"))
    })
    private PositionExposure exposure;

    @Embedded
    @JsonProperty("positiontypeDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "positionType", column = @Column(name = "position_type"))
    })
    private PositionType positionType;

    @Embedded
    @JsonProperty("indexDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "indexName", column = @Column(name = "index_name"))
    })
    private Index index;

    @Embedded
    @JsonProperty("accountDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "lei", column = @Column(name = "account_lei")),
        @AttributeOverride(name = "shortName", column = @Column(name = "short_name")),
        @AttributeOverride(name = "accountId", column = @Column(name = "account_id")),
        @AttributeOverride(name = "info", column = @Column(name = "info", insertable = false, updatable = false))
    })
    private PositionAccount positionAccount;

    @Embedded
    @JsonProperty("counterPartyDTO")
    @AttributeOverrides({
        @AttributeOverride(name = "lei", column = @Column(name = "cp_lei")),
        @AttributeOverride(name = "accountId", column = @Column(name = "cp_account_id")),
        @AttributeOverride(name = "shortName", column = @Column(name = "short_name", insertable = false, updatable = false))
    })
    private PositionAccount positionCpAccount;

    @Embedded
    @JsonProperty("statusDTO")
    private PositionStatus positionStatus;


}
