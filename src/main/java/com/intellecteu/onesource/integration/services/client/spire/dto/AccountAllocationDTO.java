/*
 * Spire-Trade-Service
 * Spire-Trade-REST-API
 *
 * OpenAPI spec version: 4.0
 * Contact: support.stonewain.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.intellecteu.onesource.integration.services.client.spire.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * AccountAllocationDTO
 */

public class AccountAllocationDTO {
  @JsonProperty("accountDTO")
  private AccountDTO accountDTO = null;

  @JsonProperty("accountId")
  private Integer accountId = null;

  @JsonProperty("accountLegalName")
  private String accountLegalName = null;

  @JsonProperty("accountName")
  private String accountName = null;

  @JsonProperty("accountNo2")
  private String accountNo2 = null;

  @JsonProperty("accountNumber")
  private String accountNumber = null;

  @JsonProperty("allocated")
  private Long allocated = null;

  @JsonProperty("allocationId")
  private Long allocationId = null;

  @JsonProperty("amount")
  private Double amount = null;

  @JsonProperty("available")
  private Long available = null;

  @JsonProperty("bias")
  private Double bias = null;

  @JsonProperty("comments")
  private String comments = null;

  @JsonProperty("currentAvailable")
  private Long currentAvailable = null;

  @JsonProperty("customValue1")
  private String customValue1 = null;

  @JsonProperty("customValue3")
  private String customValue3 = null;

  @JsonProperty("hasExistingPosition")
  private Boolean hasExistingPosition = null;

  @JsonProperty("inventory")
  private Long inventory = null;

  @JsonProperty("lendablePct")
  private Integer lendablePct = null;

  @JsonProperty("limit")
  private Double limit = null;

  @JsonProperty("maxSettleDate")
  private LocalDateTime maxSettleDate = null;

  @JsonProperty("onLoan")
  private Long onLoan = null;

  @JsonProperty("pendingQty")
  private Long pendingQty = null;

  @JsonProperty("positionId")
  private Long positionId = null;

  @JsonProperty("positionRef")
  private String positionRef = null;

  @JsonProperty("previousAllocation")
  private Long previousAllocation = null;

  @JsonProperty("quantity")
  private Double quantity = null;

  @JsonProperty("recalled")
  private Long recalled = null;

  @JsonProperty("receiverAccountId")
  private Integer receiverAccountId = null;

  @JsonProperty("receiverAccountName")
  private String receiverAccountName = null;

  @JsonProperty("receiverAccountNo2")
  private String receiverAccountNo2 = null;

  @JsonProperty("receiverAccountNumber")
  private String receiverAccountNumber = null;

  @JsonProperty("restricted")
  private Long restricted = null;

  @JsonProperty("restrictionId")
  private Integer restrictionId = null;

  @JsonProperty("sameDayQty")
  private Long sameDayQty = null;

  @JsonProperty("sequenceNumber")
  private String sequenceNumber = null;

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("suggestedAllocation")
  private Long suggestedAllocation = null;

  @JsonProperty("useDefaultReceiverAccount")
  private Boolean useDefaultReceiverAccount = null;

  public AccountAllocationDTO accountDTO(AccountDTO accountDTO) {
    this.accountDTO = accountDTO;
    return this;
  }

   /**
   * Get accountDTO
   * @return accountDTO
  **/
  @ApiModelProperty(value = "")
  public AccountDTO getAccountDTO() {
    return accountDTO;
  }

  public void setAccountDTO(AccountDTO accountDTO) {
    this.accountDTO = accountDTO;
  }

  public AccountAllocationDTO accountId(Integer accountId) {
    this.accountId = accountId;
    return this;
  }

   /**
   * Required only when posting a position &amp; Either accountId or accountNumber or accountNo2 or accountName is required
   * @return accountId
  **/
  @ApiModelProperty(required = true, value = "Required only when posting a position & Either accountId or accountNumber or accountNo2 or accountName is required")
  public Integer getAccountId() {
    return accountId;
  }

  public void setAccountId(Integer accountId) {
    this.accountId = accountId;
  }

  public AccountAllocationDTO accountLegalName(String accountLegalName) {
    this.accountLegalName = accountLegalName;
    return this;
  }

   /**
   * Get accountLegalName
   * @return accountLegalName
  **/
  @ApiModelProperty(value = "")
  public String getAccountLegalName() {
    return accountLegalName;
  }

  public void setAccountLegalName(String accountLegalName) {
    this.accountLegalName = accountLegalName;
  }

  public AccountAllocationDTO accountName(String accountName) {
    this.accountName = accountName;
    return this;
  }

   /**
   * Required only when posting a position &amp; Required only when posting a position Either accountId or accountNumber or accountNo2 or accountName is required
   * @return accountName
  **/
  @ApiModelProperty(required = true, value = "Required only when posting a position & Required only when posting a position Either accountId or accountNumber or accountNo2 or accountName is required")
  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public AccountAllocationDTO accountNo2(String accountNo2) {
    this.accountNo2 = accountNo2;
    return this;
  }

   /**
   * Required only when posting a position &amp; Either accountId or accountNumber or accountNo2 or accountName is required
   * @return accountNo2
  **/
  @ApiModelProperty(required = true, value = "Required only when posting a position & Either accountId or accountNumber or accountNo2 or accountName is required")
  public String getAccountNo2() {
    return accountNo2;
  }

  public void setAccountNo2(String accountNo2) {
    this.accountNo2 = accountNo2;
  }

  public AccountAllocationDTO accountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
    return this;
  }

   /**
   * Required only when posting a position &amp; Either accountId or accountNumber or accountNo2 or accountName is required
   * @return accountNumber
  **/
  @ApiModelProperty(required = true, value = "Required only when posting a position & Either accountId or accountNumber or accountNo2 or accountName is required")
  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public AccountAllocationDTO allocated(Long allocated) {
    this.allocated = allocated;
    return this;
  }

   /**
   * Get allocated
   * @return allocated
  **/
  @ApiModelProperty(required = true, value = "")
  public Long getAllocated() {
    return allocated;
  }

  public void setAllocated(Long allocated) {
    this.allocated = allocated;
  }

  public AccountAllocationDTO allocationId(Long allocationId) {
    this.allocationId = allocationId;
    return this;
  }

   /**
   * Get allocationId
   * @return allocationId
  **/
  @ApiModelProperty(value = "")
  public Long getAllocationId() {
    return allocationId;
  }

  public void setAllocationId(Long allocationId) {
    this.allocationId = allocationId;
  }

  public AccountAllocationDTO amount(Double amount) {
    this.amount = amount;
    return this;
  }

   /**
   * Get amount
   * @return amount
  **/
  @ApiModelProperty(value = "")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public AccountAllocationDTO available(Long available) {
    this.available = available;
    return this;
  }

   /**
   * Get available
   * @return available
  **/
  @ApiModelProperty(value = "")
  public Long getAvailable() {
    return available;
  }

  public void setAvailable(Long available) {
    this.available = available;
  }

  public AccountAllocationDTO bias(Double bias) {
    this.bias = bias;
    return this;
  }

   /**
   * Get bias
   * @return bias
  **/
  @ApiModelProperty(value = "")
  public Double getBias() {
    return bias;
  }

  public void setBias(Double bias) {
    this.bias = bias;
  }

  public AccountAllocationDTO comments(String comments) {
    this.comments = comments;
    return this;
  }

   /**
   * Get comments
   * @return comments
  **/
  @ApiModelProperty(value = "")
  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public AccountAllocationDTO currentAvailable(Long currentAvailable) {
    this.currentAvailable = currentAvailable;
    return this;
  }

   /**
   * Get currentAvailable
   * @return currentAvailable
  **/
  @ApiModelProperty(value = "")
  public Long getCurrentAvailable() {
    return currentAvailable;
  }

  public void setCurrentAvailable(Long currentAvailable) {
    this.currentAvailable = currentAvailable;
  }

  public AccountAllocationDTO customValue1(String customValue1) {
    this.customValue1 = customValue1;
    return this;
  }

   /**
   * Get customValue1
   * @return customValue1
  **/
  @ApiModelProperty(value = "")
  public String getCustomValue1() {
    return customValue1;
  }

  public void setCustomValue1(String customValue1) {
    this.customValue1 = customValue1;
  }

  public AccountAllocationDTO customValue3(String customValue3) {
    this.customValue3 = customValue3;
    return this;
  }

   /**
   * Get customValue3
   * @return customValue3
  **/
  @ApiModelProperty(value = "")
  public String getCustomValue3() {
    return customValue3;
  }

  public void setCustomValue3(String customValue3) {
    this.customValue3 = customValue3;
  }

  public AccountAllocationDTO hasExistingPosition(Boolean hasExistingPosition) {
    this.hasExistingPosition = hasExistingPosition;
    return this;
  }

   /**
   * Get hasExistingPosition
   * @return hasExistingPosition
  **/
  @ApiModelProperty(value = "")
  public Boolean isHasExistingPosition() {
    return hasExistingPosition;
  }

  public void setHasExistingPosition(Boolean hasExistingPosition) {
    this.hasExistingPosition = hasExistingPosition;
  }

  public AccountAllocationDTO inventory(Long inventory) {
    this.inventory = inventory;
    return this;
  }

   /**
   * Get inventory
   * @return inventory
  **/
  @ApiModelProperty(value = "")
  public Long getInventory() {
    return inventory;
  }

  public void setInventory(Long inventory) {
    this.inventory = inventory;
  }

  public AccountAllocationDTO lendablePct(Integer lendablePct) {
    this.lendablePct = lendablePct;
    return this;
  }

   /**
   * Get lendablePct
   * @return lendablePct
  **/
  @ApiModelProperty(value = "")
  public Integer getLendablePct() {
    return lendablePct;
  }

  public void setLendablePct(Integer lendablePct) {
    this.lendablePct = lendablePct;
  }

  public AccountAllocationDTO limit(Double limit) {
    this.limit = limit;
    return this;
  }

   /**
   * Get limit
   * @return limit
  **/
  @ApiModelProperty(value = "")
  public Double getLimit() {
    return limit;
  }

  public void setLimit(Double limit) {
    this.limit = limit;
  }

  public AccountAllocationDTO maxSettleDate(LocalDateTime maxSettleDate) {
    this.maxSettleDate = maxSettleDate;
    return this;
  }

   /**
   * Get maxSettleDate
   * @return maxSettleDate
  **/
  @ApiModelProperty(value = "")
  public LocalDateTime getMaxSettleDate() {
    return maxSettleDate;
  }

  public void setMaxSettleDate(LocalDateTime maxSettleDate) {
    this.maxSettleDate = maxSettleDate;
  }

  public AccountAllocationDTO onLoan(Long onLoan) {
    this.onLoan = onLoan;
    return this;
  }

   /**
   * Get onLoan
   * @return onLoan
  **/
  @ApiModelProperty(value = "")
  public Long getOnLoan() {
    return onLoan;
  }

  public void setOnLoan(Long onLoan) {
    this.onLoan = onLoan;
  }

  public AccountAllocationDTO pendingQty(Long pendingQty) {
    this.pendingQty = pendingQty;
    return this;
  }

   /**
   * Get pendingQty
   * @return pendingQty
  **/
  @ApiModelProperty(value = "")
  public Long getPendingQty() {
    return pendingQty;
  }

  public void setPendingQty(Long pendingQty) {
    this.pendingQty = pendingQty;
  }

  public AccountAllocationDTO positionId(Long positionId) {
    this.positionId = positionId;
    return this;
  }

   /**
   * Get positionId
   * @return positionId
  **/
  @ApiModelProperty(value = "")
  public Long getPositionId() {
    return positionId;
  }

  public void setPositionId(Long positionId) {
    this.positionId = positionId;
  }

  public AccountAllocationDTO positionRef(String positionRef) {
    this.positionRef = positionRef;
    return this;
  }

   /**
   * Get positionRef
   * @return positionRef
  **/
  @ApiModelProperty(value = "")
  public String getPositionRef() {
    return positionRef;
  }

  public void setPositionRef(String positionRef) {
    this.positionRef = positionRef;
  }

  public AccountAllocationDTO previousAllocation(Long previousAllocation) {
    this.previousAllocation = previousAllocation;
    return this;
  }

   /**
   * Get previousAllocation
   * @return previousAllocation
  **/
  @ApiModelProperty(value = "")
  public Long getPreviousAllocation() {
    return previousAllocation;
  }

  public void setPreviousAllocation(Long previousAllocation) {
    this.previousAllocation = previousAllocation;
  }

  public AccountAllocationDTO quantity(Double quantity) {
    this.quantity = quantity;
    return this;
  }

   /**
   * Get quantity
   * @return quantity
  **/
  @ApiModelProperty(value = "")
  public Double getQuantity() {
    return quantity;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }

  public AccountAllocationDTO recalled(Long recalled) {
    this.recalled = recalled;
    return this;
  }

   /**
   * Get recalled
   * @return recalled
  **/
  @ApiModelProperty(value = "")
  public Long getRecalled() {
    return recalled;
  }

  public void setRecalled(Long recalled) {
    this.recalled = recalled;
  }

  public AccountAllocationDTO receiverAccountId(Integer receiverAccountId) {
    this.receiverAccountId = receiverAccountId;
    return this;
  }

   /**
   * Required only when posting a position &amp; Either receiverAccountId or receiverAccountNumber or receiverAccountNo2 or receiverAccountName or useDefaultReceiverAccount is required
   * @return receiverAccountId
  **/
  @ApiModelProperty(required = true, value = "Required only when posting a position & Either receiverAccountId or receiverAccountNumber or receiverAccountNo2 or receiverAccountName or useDefaultReceiverAccount is required")
  public Integer getReceiverAccountId() {
    return receiverAccountId;
  }

  public void setReceiverAccountId(Integer receiverAccountId) {
    this.receiverAccountId = receiverAccountId;
  }

  public AccountAllocationDTO receiverAccountName(String receiverAccountName) {
    this.receiverAccountName = receiverAccountName;
    return this;
  }

   /**
   * Required only when posting a position &amp; Either receiverAccountId or receiverAccountNumber or receiverAccountNo2 or receiverAccountName or useDefaultReceiverAccount is required
   * @return receiverAccountName
  **/
  @ApiModelProperty(required = true, value = "Required only when posting a position & Either receiverAccountId or receiverAccountNumber or receiverAccountNo2 or receiverAccountName or useDefaultReceiverAccount is required")
  public String getReceiverAccountName() {
    return receiverAccountName;
  }

  public void setReceiverAccountName(String receiverAccountName) {
    this.receiverAccountName = receiverAccountName;
  }

  public AccountAllocationDTO receiverAccountNo2(String receiverAccountNo2) {
    this.receiverAccountNo2 = receiverAccountNo2;
    return this;
  }

   /**
   * Required only when posting a position &amp; Either receiverAccountId or receiverAccountNumber or receiverAccountNo2 or receiverAccountName or useDefaultReceiverAccount is required
   * @return receiverAccountNo2
  **/
  @ApiModelProperty(required = true, value = "Required only when posting a position & Either receiverAccountId or receiverAccountNumber or receiverAccountNo2 or receiverAccountName or useDefaultReceiverAccount is required")
  public String getReceiverAccountNo2() {
    return receiverAccountNo2;
  }

  public void setReceiverAccountNo2(String receiverAccountNo2) {
    this.receiverAccountNo2 = receiverAccountNo2;
  }

  public AccountAllocationDTO receiverAccountNumber(String receiverAccountNumber) {
    this.receiverAccountNumber = receiverAccountNumber;
    return this;
  }

   /**
   * Required only when posting a position &amp; Either receiverAccountId or receiverAccountNumber or receiverAccountNo2 or receiverAccountName or useDefaultReceiverAccount is required
   * @return receiverAccountNumber
  **/
  @ApiModelProperty(required = true, value = "Required only when posting a position & Either receiverAccountId or receiverAccountNumber or receiverAccountNo2 or receiverAccountName or useDefaultReceiverAccount is required")
  public String getReceiverAccountNumber() {
    return receiverAccountNumber;
  }

  public void setReceiverAccountNumber(String receiverAccountNumber) {
    this.receiverAccountNumber = receiverAccountNumber;
  }

  public AccountAllocationDTO restricted(Long restricted) {
    this.restricted = restricted;
    return this;
  }

   /**
   * Get restricted
   * @return restricted
  **/
  @ApiModelProperty(value = "")
  public Long getRestricted() {
    return restricted;
  }

  public void setRestricted(Long restricted) {
    this.restricted = restricted;
  }

  public AccountAllocationDTO restrictionId(Integer restrictionId) {
    this.restrictionId = restrictionId;
    return this;
  }

   /**
   * Get restrictionId
   * @return restrictionId
  **/
  @ApiModelProperty(value = "")
  public Integer getRestrictionId() {
    return restrictionId;
  }

  public void setRestrictionId(Integer restrictionId) {
    this.restrictionId = restrictionId;
  }

  public AccountAllocationDTO sameDayQty(Long sameDayQty) {
    this.sameDayQty = sameDayQty;
    return this;
  }

   /**
   * Get sameDayQty
   * @return sameDayQty
  **/
  @ApiModelProperty(value = "")
  public Long getSameDayQty() {
    return sameDayQty;
  }

  public void setSameDayQty(Long sameDayQty) {
    this.sameDayQty = sameDayQty;
  }

  public AccountAllocationDTO sequenceNumber(String sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
    return this;
  }

   /**
   * Get sequenceNumber
   * @return sequenceNumber
  **/
  @ApiModelProperty(value = "")
  public String getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(String sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public AccountAllocationDTO status(String status) {
    this.status = status;
    return this;
  }

   /**
   * Get status
   * @return status
  **/
  @ApiModelProperty(value = "")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public AccountAllocationDTO suggestedAllocation(Long suggestedAllocation) {
    this.suggestedAllocation = suggestedAllocation;
    return this;
  }

   /**
   * Get suggestedAllocation
   * @return suggestedAllocation
  **/
  @ApiModelProperty(value = "")
  public Long getSuggestedAllocation() {
    return suggestedAllocation;
  }

  public void setSuggestedAllocation(Long suggestedAllocation) {
    this.suggestedAllocation = suggestedAllocation;
  }

  public AccountAllocationDTO useDefaultReceiverAccount(Boolean useDefaultReceiverAccount) {
    this.useDefaultReceiverAccount = useDefaultReceiverAccount;
    return this;
  }

   /**
   * Required only when posting a position &amp; Either receiverAccountId or receiverAccountNumber or receiverAccountNo2 or receiverAccountName or useDefaultReceiverAccount is required
   * @return useDefaultReceiverAccount
  **/
  @ApiModelProperty(required = true, value = "Required only when posting a position & Either receiverAccountId or receiverAccountNumber or receiverAccountNo2 or receiverAccountName or useDefaultReceiverAccount is required")
  public Boolean isUseDefaultReceiverAccount() {
    return useDefaultReceiverAccount;
  }

  public void setUseDefaultReceiverAccount(Boolean useDefaultReceiverAccount) {
    this.useDefaultReceiverAccount = useDefaultReceiverAccount;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountAllocationDTO accountAllocationDTO = (AccountAllocationDTO) o;
    return Objects.equals(this.accountDTO, accountAllocationDTO.accountDTO) &&
        Objects.equals(this.accountId, accountAllocationDTO.accountId) &&
        Objects.equals(this.accountLegalName, accountAllocationDTO.accountLegalName) &&
        Objects.equals(this.accountName, accountAllocationDTO.accountName) &&
        Objects.equals(this.accountNo2, accountAllocationDTO.accountNo2) &&
        Objects.equals(this.accountNumber, accountAllocationDTO.accountNumber) &&
        Objects.equals(this.allocated, accountAllocationDTO.allocated) &&
        Objects.equals(this.allocationId, accountAllocationDTO.allocationId) &&
        Objects.equals(this.amount, accountAllocationDTO.amount) &&
        Objects.equals(this.available, accountAllocationDTO.available) &&
        Objects.equals(this.bias, accountAllocationDTO.bias) &&
        Objects.equals(this.comments, accountAllocationDTO.comments) &&
        Objects.equals(this.currentAvailable, accountAllocationDTO.currentAvailable) &&
        Objects.equals(this.customValue1, accountAllocationDTO.customValue1) &&
        Objects.equals(this.customValue3, accountAllocationDTO.customValue3) &&
        Objects.equals(this.hasExistingPosition, accountAllocationDTO.hasExistingPosition) &&
        Objects.equals(this.inventory, accountAllocationDTO.inventory) &&
        Objects.equals(this.lendablePct, accountAllocationDTO.lendablePct) &&
        Objects.equals(this.limit, accountAllocationDTO.limit) &&
        Objects.equals(this.maxSettleDate, accountAllocationDTO.maxSettleDate) &&
        Objects.equals(this.onLoan, accountAllocationDTO.onLoan) &&
        Objects.equals(this.pendingQty, accountAllocationDTO.pendingQty) &&
        Objects.equals(this.positionId, accountAllocationDTO.positionId) &&
        Objects.equals(this.positionRef, accountAllocationDTO.positionRef) &&
        Objects.equals(this.previousAllocation, accountAllocationDTO.previousAllocation) &&
        Objects.equals(this.quantity, accountAllocationDTO.quantity) &&
        Objects.equals(this.recalled, accountAllocationDTO.recalled) &&
        Objects.equals(this.receiverAccountId, accountAllocationDTO.receiverAccountId) &&
        Objects.equals(this.receiverAccountName, accountAllocationDTO.receiverAccountName) &&
        Objects.equals(this.receiverAccountNo2, accountAllocationDTO.receiverAccountNo2) &&
        Objects.equals(this.receiverAccountNumber, accountAllocationDTO.receiverAccountNumber) &&
        Objects.equals(this.restricted, accountAllocationDTO.restricted) &&
        Objects.equals(this.restrictionId, accountAllocationDTO.restrictionId) &&
        Objects.equals(this.sameDayQty, accountAllocationDTO.sameDayQty) &&
        Objects.equals(this.sequenceNumber, accountAllocationDTO.sequenceNumber) &&
        Objects.equals(this.status, accountAllocationDTO.status) &&
        Objects.equals(this.suggestedAllocation, accountAllocationDTO.suggestedAllocation) &&
        Objects.equals(this.useDefaultReceiverAccount, accountAllocationDTO.useDefaultReceiverAccount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountDTO, accountId, accountLegalName, accountName, accountNo2, accountNumber, allocated, allocationId, amount, available, bias, comments, currentAvailable, customValue1, customValue3, hasExistingPosition, inventory, lendablePct, limit, maxSettleDate, onLoan, pendingQty, positionId, positionRef, previousAllocation, quantity, recalled, receiverAccountId, receiverAccountName, receiverAccountNo2, receiverAccountNumber, restricted, restrictionId, sameDayQty, sequenceNumber, status, suggestedAllocation, useDefaultReceiverAccount);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountAllocationDTO {\n");
    
    sb.append("    accountDTO: ").append(toIndentedString(accountDTO)).append("\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    accountLegalName: ").append(toIndentedString(accountLegalName)).append("\n");
    sb.append("    accountName: ").append(toIndentedString(accountName)).append("\n");
    sb.append("    accountNo2: ").append(toIndentedString(accountNo2)).append("\n");
    sb.append("    accountNumber: ").append(toIndentedString(accountNumber)).append("\n");
    sb.append("    allocated: ").append(toIndentedString(allocated)).append("\n");
    sb.append("    allocationId: ").append(toIndentedString(allocationId)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    available: ").append(toIndentedString(available)).append("\n");
    sb.append("    bias: ").append(toIndentedString(bias)).append("\n");
    sb.append("    comments: ").append(toIndentedString(comments)).append("\n");
    sb.append("    currentAvailable: ").append(toIndentedString(currentAvailable)).append("\n");
    sb.append("    customValue1: ").append(toIndentedString(customValue1)).append("\n");
    sb.append("    customValue3: ").append(toIndentedString(customValue3)).append("\n");
    sb.append("    hasExistingPosition: ").append(toIndentedString(hasExistingPosition)).append("\n");
    sb.append("    inventory: ").append(toIndentedString(inventory)).append("\n");
    sb.append("    lendablePct: ").append(toIndentedString(lendablePct)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    maxSettleDate: ").append(toIndentedString(maxSettleDate)).append("\n");
    sb.append("    onLoan: ").append(toIndentedString(onLoan)).append("\n");
    sb.append("    pendingQty: ").append(toIndentedString(pendingQty)).append("\n");
    sb.append("    positionId: ").append(toIndentedString(positionId)).append("\n");
    sb.append("    positionRef: ").append(toIndentedString(positionRef)).append("\n");
    sb.append("    previousAllocation: ").append(toIndentedString(previousAllocation)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    recalled: ").append(toIndentedString(recalled)).append("\n");
    sb.append("    receiverAccountId: ").append(toIndentedString(receiverAccountId)).append("\n");
    sb.append("    receiverAccountName: ").append(toIndentedString(receiverAccountName)).append("\n");
    sb.append("    receiverAccountNo2: ").append(toIndentedString(receiverAccountNo2)).append("\n");
    sb.append("    receiverAccountNumber: ").append(toIndentedString(receiverAccountNumber)).append("\n");
    sb.append("    restricted: ").append(toIndentedString(restricted)).append("\n");
    sb.append("    restrictionId: ").append(toIndentedString(restrictionId)).append("\n");
    sb.append("    sameDayQty: ").append(toIndentedString(sameDayQty)).append("\n");
    sb.append("    sequenceNumber: ").append(toIndentedString(sequenceNumber)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    suggestedAllocation: ").append(toIndentedString(suggestedAllocation)).append("\n");
    sb.append("    useDefaultReceiverAccount: ").append(toIndentedString(useDefaultReceiverAccount)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

