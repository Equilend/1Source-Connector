package com.intellecteu.onesource.integration.model.backoffice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import java.time.LocalDateTime;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Position {

    private String positionId;
    private String venueRefId;
    private String customValue2;
    private String positionRef;
    private Double rate;
    private Double quantity;
    private LocalDateTime tradeDate;
    private Integer termId;
    private LocalDateTime endDate;
    private LocalDateTime settleDate;
    private Boolean deliverFree;
    private Double amount;
    private Double price;
    private Double contractValue;
    private Integer currencyId;
    private Long securityId;
    private Integer positionTypeId;
    private ProcessingStatus processingStatus;
    private LocalDateTime lastUpdateDateTime;
    private String matching1SourceTradeAgreementId;
    private String matching1SourceLoanContractId;
    private Long applicableInstructionId;
    @JsonProperty("securityDetailDTO")
    private PositionSecurityDetail positionSecurityDetail;
    @JsonProperty("currencyDTO")
    private Currency currency;
    @JsonProperty("loanBorrowDTO")
    private LoanBorrow loanBorrow;
    @JsonProperty("collateralTypeDTO")
    private PositionCollateralType positionCollateralType;
    @JsonProperty("exposureDTO")
    private PositionExposure exposure;
    @JsonProperty("positiontypeDTO")
    private PositionType positionType;
    @JsonProperty("indexDTO")
    private Index index;
    @JsonProperty("accountDTO")
    private PositionAccount positionAccount;
    @JsonProperty("counterPartyDTO")
    private PositionAccount positionCpAccount;
    @JsonProperty("statusDTO")
    private PositionStatus positionStatus;

}
