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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RecallDTO
 */

public class RecallDTO {
  @JsonProperty("accountAllocationDTOs")
  private List<AccountAllocationDTO> accountAllocationDTOs = null;

  @JsonProperty("accountDTO")
  private AccountDTO accountDTO = null;

  @JsonProperty("accountId")
  private Long accountId = null;

  @JsonProperty("caller")
  private Boolean caller = null;

  @JsonProperty("comments")
  private String comments = null;

  @JsonProperty("counterpartyId")
  private Long counterpartyId = null;

  @JsonProperty("createTs")
  private LocalDateTime createTs = null;

  @JsonProperty("createUserId")
  private Long createUserId = null;

  @JsonProperty("depoId")
  private Integer depoId = null;

  @JsonProperty("isCaller")
  private Boolean isCaller = null;

  @JsonProperty("lastModTs")
  private LocalDateTime lastModTs = null;

  @JsonProperty("lastModUserId")
  private Long lastModUserId = null;

  @JsonProperty("lendingAgentId")
  private Long lendingAgentId = null;

  @JsonProperty("poolingAccountId")
  private Long poolingAccountId = null;

  @JsonProperty("positionId")
  private Long positionId = null;

  @JsonProperty("positionRef")
  private String positionRef = null;

  @JsonProperty("rateChange")
  private Boolean rateChange = null;

  /**
   * Gets or Sets rateChangeIndex
   */
  public enum RateChangeIndexEnum {
    ONE_MONTH_LIBOR("ONE_MONTH_LIBOR"),
    
    THREE_MONTH_LIBOR("THREE_MONTH_LIBOR"),
    
    FED_FUNDS_EFFECTIVE("FED_FUNDS_EFFECTIVE"),
    
    FED_FUNDS_OPEN("FED_FUNDS_OPEN"),
    
    NA("NA"),
    
    FIXED_RATE("FIXED_RATE"),
    
    USD_ONE_MONTH_LIBOR("USD_ONE_MONTH_LIBOR"),
    
    USD_THREE_MONTH_LIBOR("USD_THREE_MONTH_LIBOR"),
    
    EUR_ONE_MONTH_LIBOR("EUR_ONE_MONTH_LIBOR"),
    
    EUR_THREE_MONTH_LIBOR("EUR_THREE_MONTH_LIBOR"),
    
    EUR_ONE_MONTH_EURIBOR("EUR_ONE_MONTH_EURIBOR"),
    
    EUR_THREE_MONTH_EURIBOR("EUR_THREE_MONTH_EURIBOR"),
    
    CNY_ONE_MONTH_SHIBOR("CNY_ONE_MONTH_SHIBOR"),
    
    CNY_THREE_MONTH_SHIBOR("CNY_THREE_MONTH_SHIBOR"),
    
    JPY_ONE_WEEK_TIBOR("JPY_ONE_WEEK_TIBOR"),
    
    JPY_TWO_MONTH_TIBOR("JPY_TWO_MONTH_TIBOR"),
    
    TWD_ONE_WEEK_TAIBOR("TWD_ONE_WEEK_TAIBOR"),
    
    TWD_ONE_MONTH_TAIBOR("TWD_ONE_MONTH_TAIBOR"),
    
    SEK_ONE_MONTH_STIBOR("SEK_ONE_MONTH_STIBOR"),
    
    SEK_THREE_MONTH_STIBOR("SEK_THREE_MONTH_STIBOR"),
    
    STIBOR("STIBOR"),
    
    TAI_ONE_MONTH_TAIBOR("TAI_ONE_MONTH_TAIBOR"),
    
    ESTR("ESTR"),
    
    JEFFRIES("JEFFRIES"),
    
    OBFR("OBFR"),
    
    SWING_RATE("SWING_RATE"),
    
    TESTING("TESTING");

    private String value;

    RateChangeIndexEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static RateChangeIndexEnum fromValue(String value) {
      for (RateChangeIndexEnum b : RateChangeIndexEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("rateChangeIndex")
  private RateChangeIndexEnum rateChangeIndex = null;

  @JsonProperty("rateChangeIndexId")
  private Integer rateChangeIndexId = null;

  @JsonProperty("rateChangeRate")
  private Double rateChangeRate = null;

  @JsonProperty("rateChangeSpread")
  private Double rateChangeSpread = null;

  @JsonProperty("recallDate")
  private LocalDateTime recallDate = null;

  @JsonProperty("recallDueDate")
  private LocalDateTime recallDueDate = null;

  @JsonProperty("recallId")
  private Long recallId = null;

  @JsonProperty("recallPositionDTO")
  private RecallPositionDTO recallPositionDTO = null;

  @JsonProperty("recallPositionDTOs")
  private List<RecallPositionDTO> recallPositionDTOs = null;

  /**
   * Gets or Sets recallReason
   */
  public enum RecallReasonEnum {
    SEG_DEFICIT("SEG_DEFICIT"),
    
    RETRANSMITTAL("RETRANSMITTAL"),
    
    CORP_ACTION("CORP_ACTION"),
    
    PENDING_SALE("PENDING_SALE"),
    
    FIRM_FAIL("FIRM_FAIL"),
    
    TERMINATION("TERMINATION"),
    
    MARGINABILITY("MARGINABILITY"),
    
    OTHER("OTHER"),
    
    UNKNOWN("UNKNOWN");

    private String value;

    RecallReasonEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static RecallReasonEnum fromValue(String value) {
      for (RecallReasonEnum b : RecallReasonEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("recallReason")
  private RecallReasonEnum recallReason = null;

  @JsonProperty("recallReasonId")
  private Long recallReasonId = null;

  /**
   * Status of the recall.
   */
  public enum RecallStatusEnum {
    CLOSED("CLOSED"),
    
    NEW("NEW"),
    
    DELETED("DELETED"),
    
    COMPLETE("COMPLETE");

    private String value;

    RecallStatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static RecallStatusEnum fromValue(String value) {
      for (RecallStatusEnum b : RecallStatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("recallStatus")
  private RecallStatusEnum recallStatus = null;

  @JsonProperty("recallStatusId")
  private Integer recallStatusId = null;

  @JsonProperty("recalled")
  private Long recalled = null;

  @JsonProperty("restrictInventory")
  private Boolean restrictInventory = null;

  @JsonProperty("saleQuantity")
  private Long saleQuantity = null;

  @JsonProperty("securityDetailDTO")
  private SecurityDetailDTO securityDetailDTO = null;

  @JsonProperty("securityId")
  private Long securityId = null;

  @JsonProperty("settleDate")
  private LocalDateTime settleDate = null;

  @JsonProperty("statusDTO")
  private StatusDTO statusDTO = null;

  @JsonProperty("subAccountId")
  private Long subAccountId = null;

  @JsonProperty("systemRefId")
  private Integer systemRefId = null;

  @JsonProperty("tradeDate")
  private LocalDateTime tradeDate = null;

  public RecallDTO accountAllocationDTOs(List<AccountAllocationDTO> accountAllocationDTOs) {
    this.accountAllocationDTOs = accountAllocationDTOs;
    return this;
  }

  public RecallDTO addAccountAllocationDTOsItem(AccountAllocationDTO accountAllocationDTOsItem) {
    if (this.accountAllocationDTOs == null) {
      this.accountAllocationDTOs = new ArrayList<>();
    }
    this.accountAllocationDTOs.add(accountAllocationDTOsItem);
    return this;
  }

   /**
   * Get accountAllocationDTOs
   * @return accountAllocationDTOs
  **/
  @ApiModelProperty(value = "")
  public List<AccountAllocationDTO> getAccountAllocationDTOs() {
    return accountAllocationDTOs;
  }

  public void setAccountAllocationDTOs(List<AccountAllocationDTO> accountAllocationDTOs) {
    this.accountAllocationDTOs = accountAllocationDTOs;
  }

  public RecallDTO accountDTO(AccountDTO accountDTO) {
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

  public RecallDTO accountId(Long accountId) {
    this.accountId = accountId;
    return this;
  }

   /**
   * Get accountId
   * @return accountId
  **/
  @ApiModelProperty(value = "")
  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public RecallDTO caller(Boolean caller) {
    this.caller = caller;
    return this;
  }

   /**
   * Get caller
   * @return caller
  **/
  @ApiModelProperty(value = "")
  public Boolean isCaller() {
    return caller;
  }

  public void setCaller(Boolean caller) {
    this.caller = caller;
  }

  public RecallDTO comments(String comments) {
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

  public RecallDTO counterpartyId(Long counterpartyId) {
    this.counterpartyId = counterpartyId;
    return this;
  }

   /**
   * Get counterpartyId
   * @return counterpartyId
  **/
  @ApiModelProperty(value = "")
  public Long getCounterpartyId() {
    return counterpartyId;
  }

  public void setCounterpartyId(Long counterpartyId) {
    this.counterpartyId = counterpartyId;
  }

  public RecallDTO createTs(LocalDateTime createTs) {
    this.createTs = createTs;
    return this;
  }

   /**
   * Get createTs
   * @return createTs
  **/
  @ApiModelProperty(value = "")
  public LocalDateTime getCreateTs() {
    return createTs;
  }

  public void setCreateTs(LocalDateTime createTs) {
    this.createTs = createTs;
  }

  public RecallDTO createUserId(Long createUserId) {
    this.createUserId = createUserId;
    return this;
  }

   /**
   * Get createUserId
   * @return createUserId
  **/
  @ApiModelProperty(value = "")
  public Long getCreateUserId() {
    return createUserId;
  }

  public void setCreateUserId(Long createUserId) {
    this.createUserId = createUserId;
  }

  public RecallDTO depoId(Integer depoId) {
    this.depoId = depoId;
    return this;
  }

   /**
   * Get depoId
   * @return depoId
  **/
  @ApiModelProperty(value = "")
  public Integer getDepoId() {
    return depoId;
  }

  public void setDepoId(Integer depoId) {
    this.depoId = depoId;
  }

  public RecallDTO isCaller(Boolean isCaller) {
    this.isCaller = isCaller;
    return this;
  }

   /**
   * Get isCaller
   * @return isCaller
  **/
  @ApiModelProperty(value = "")
  public Boolean isIsCaller() {
    return isCaller;
  }

  public void setIsCaller(Boolean isCaller) {
    this.isCaller = isCaller;
  }

  public RecallDTO lastModTs(LocalDateTime lastModTs) {
    this.lastModTs = lastModTs;
    return this;
  }

   /**
   * Get lastModTs
   * @return lastModTs
  **/
  @ApiModelProperty(value = "")
  public LocalDateTime getLastModTs() {
    return lastModTs;
  }

  public void setLastModTs(LocalDateTime lastModTs) {
    this.lastModTs = lastModTs;
  }

  public RecallDTO lastModUserId(Long lastModUserId) {
    this.lastModUserId = lastModUserId;
    return this;
  }

   /**
   * Get lastModUserId
   * @return lastModUserId
  **/
  @ApiModelProperty(value = "")
  public Long getLastModUserId() {
    return lastModUserId;
  }

  public void setLastModUserId(Long lastModUserId) {
    this.lastModUserId = lastModUserId;
  }

  public RecallDTO lendingAgentId(Long lendingAgentId) {
    this.lendingAgentId = lendingAgentId;
    return this;
  }

   /**
   * Get lendingAgentId
   * @return lendingAgentId
  **/
  @ApiModelProperty(value = "")
  public Long getLendingAgentId() {
    return lendingAgentId;
  }

  public void setLendingAgentId(Long lendingAgentId) {
    this.lendingAgentId = lendingAgentId;
  }

  public RecallDTO poolingAccountId(Long poolingAccountId) {
    this.poolingAccountId = poolingAccountId;
    return this;
  }

   /**
   * Get poolingAccountId
   * @return poolingAccountId
  **/
  @ApiModelProperty(value = "")
  public Long getPoolingAccountId() {
    return poolingAccountId;
  }

  public void setPoolingAccountId(Long poolingAccountId) {
    this.poolingAccountId = poolingAccountId;
  }

  public RecallDTO positionId(Long positionId) {
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

  public RecallDTO positionRef(String positionRef) {
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

  public RecallDTO rateChange(Boolean rateChange) {
    this.rateChange = rateChange;
    return this;
  }

   /**
   * Get rateChange
   * @return rateChange
  **/
  @ApiModelProperty(value = "")
  public Boolean isRateChange() {
    return rateChange;
  }

  public void setRateChange(Boolean rateChange) {
    this.rateChange = rateChange;
  }

  public RecallDTO rateChangeIndex(RateChangeIndexEnum rateChangeIndex) {
    this.rateChangeIndex = rateChangeIndex;
    return this;
  }

   /**
   * Get rateChangeIndex
   * @return rateChangeIndex
  **/
  @ApiModelProperty(value = "")
  public RateChangeIndexEnum getRateChangeIndex() {
    return rateChangeIndex;
  }

  public void setRateChangeIndex(RateChangeIndexEnum rateChangeIndex) {
    this.rateChangeIndex = rateChangeIndex;
  }

  public RecallDTO rateChangeIndexId(Integer rateChangeIndexId) {
    this.rateChangeIndexId = rateChangeIndexId;
    return this;
  }

   /**
   * Get rateChangeIndexId
   * @return rateChangeIndexId
  **/
  @ApiModelProperty(value = "")
  public Integer getRateChangeIndexId() {
    return rateChangeIndexId;
  }

  public void setRateChangeIndexId(Integer rateChangeIndexId) {
    this.rateChangeIndexId = rateChangeIndexId;
  }

  public RecallDTO rateChangeRate(Double rateChangeRate) {
    this.rateChangeRate = rateChangeRate;
    return this;
  }

   /**
   * Get rateChangeRate
   * @return rateChangeRate
  **/
  @ApiModelProperty(value = "")
  public Double getRateChangeRate() {
    return rateChangeRate;
  }

  public void setRateChangeRate(Double rateChangeRate) {
    this.rateChangeRate = rateChangeRate;
  }

  public RecallDTO rateChangeSpread(Double rateChangeSpread) {
    this.rateChangeSpread = rateChangeSpread;
    return this;
  }

   /**
   * Get rateChangeSpread
   * @return rateChangeSpread
  **/
  @ApiModelProperty(value = "")
  public Double getRateChangeSpread() {
    return rateChangeSpread;
  }

  public void setRateChangeSpread(Double rateChangeSpread) {
    this.rateChangeSpread = rateChangeSpread;
  }

  public RecallDTO recallDate(LocalDateTime recallDate) {
    this.recallDate = recallDate;
    return this;
  }

   /**
   * Get recallDate
   * @return recallDate
  **/
  @ApiModelProperty(value = "")
  public LocalDateTime getRecallDate() {
    return recallDate;
  }

  public void setRecallDate(LocalDateTime recallDate) {
    this.recallDate = recallDate;
  }

  public RecallDTO recallDueDate(LocalDateTime recallDueDate) {
    this.recallDueDate = recallDueDate;
    return this;
  }

   /**
   * Get recallDueDate
   * @return recallDueDate
  **/
  @ApiModelProperty(value = "")
  public LocalDateTime getRecallDueDate() {
    return recallDueDate;
  }

  public void setRecallDueDate(LocalDateTime recallDueDate) {
    this.recallDueDate = recallDueDate;
  }

  public RecallDTO recallId(Long recallId) {
    this.recallId = recallId;
    return this;
  }

   /**
   * Get recallId
   * @return recallId
  **/
  @ApiModelProperty(value = "")
  public Long getRecallId() {
    return recallId;
  }

  public void setRecallId(Long recallId) {
    this.recallId = recallId;
  }

  public RecallDTO recallPositionDTO(RecallPositionDTO recallPositionDTO) {
    this.recallPositionDTO = recallPositionDTO;
    return this;
  }

   /**
   * Get recallPositionDTO
   * @return recallPositionDTO
  **/
  @ApiModelProperty(value = "")
  public RecallPositionDTO getRecallPositionDTO() {
    return recallPositionDTO;
  }

  public void setRecallPositionDTO(RecallPositionDTO recallPositionDTO) {
    this.recallPositionDTO = recallPositionDTO;
  }

  public RecallDTO recallPositionDTOs(List<RecallPositionDTO> recallPositionDTOs) {
    this.recallPositionDTOs = recallPositionDTOs;
    return this;
  }

  public RecallDTO addRecallPositionDTOsItem(RecallPositionDTO recallPositionDTOsItem) {
    if (this.recallPositionDTOs == null) {
      this.recallPositionDTOs = new ArrayList<>();
    }
    this.recallPositionDTOs.add(recallPositionDTOsItem);
    return this;
  }

   /**
   * Get recallPositionDTOs
   * @return recallPositionDTOs
  **/
  @ApiModelProperty(value = "")
  public List<RecallPositionDTO> getRecallPositionDTOs() {
    return recallPositionDTOs;
  }

  public void setRecallPositionDTOs(List<RecallPositionDTO> recallPositionDTOs) {
    this.recallPositionDTOs = recallPositionDTOs;
  }

  public RecallDTO recallReason(RecallReasonEnum recallReason) {
    this.recallReason = recallReason;
    return this;
  }

   /**
   * Get recallReason
   * @return recallReason
  **/
  @ApiModelProperty(value = "")
  public RecallReasonEnum getRecallReason() {
    return recallReason;
  }

  public void setRecallReason(RecallReasonEnum recallReason) {
    this.recallReason = recallReason;
  }

  public RecallDTO recallReasonId(Long recallReasonId) {
    this.recallReasonId = recallReasonId;
    return this;
  }

   /**
   * Get recallReasonId
   * @return recallReasonId
  **/
  @ApiModelProperty(value = "")
  public Long getRecallReasonId() {
    return recallReasonId;
  }

  public void setRecallReasonId(Long recallReasonId) {
    this.recallReasonId = recallReasonId;
  }

  public RecallDTO recallStatus(RecallStatusEnum recallStatus) {
    this.recallStatus = recallStatus;
    return this;
  }

   /**
   * Status of the recall.
   * @return recallStatus
  **/
  @ApiModelProperty(value = "Status of the recall.")
  public RecallStatusEnum getRecallStatus() {
    return recallStatus;
  }

  public void setRecallStatus(RecallStatusEnum recallStatus) {
    this.recallStatus = recallStatus;
  }

  public RecallDTO recallStatusId(Integer recallStatusId) {
    this.recallStatusId = recallStatusId;
    return this;
  }

   /**
   * Get recallStatusId
   * @return recallStatusId
  **/
  @ApiModelProperty(value = "")
  public Integer getRecallStatusId() {
    return recallStatusId;
  }

  public void setRecallStatusId(Integer recallStatusId) {
    this.recallStatusId = recallStatusId;
  }

  public RecallDTO recalled(Long recalled) {
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

  public RecallDTO restrictInventory(Boolean restrictInventory) {
    this.restrictInventory = restrictInventory;
    return this;
  }

   /**
   * Get restrictInventory
   * @return restrictInventory
  **/
  @ApiModelProperty(value = "")
  public Boolean isRestrictInventory() {
    return restrictInventory;
  }

  public void setRestrictInventory(Boolean restrictInventory) {
    this.restrictInventory = restrictInventory;
  }

  public RecallDTO saleQuantity(Long saleQuantity) {
    this.saleQuantity = saleQuantity;
    return this;
  }

   /**
   * Get saleQuantity
   * @return saleQuantity
  **/
  @ApiModelProperty(value = "")
  public Long getSaleQuantity() {
    return saleQuantity;
  }

  public void setSaleQuantity(Long saleQuantity) {
    this.saleQuantity = saleQuantity;
  }

  public RecallDTO securityDetailDTO(SecurityDetailDTO securityDetailDTO) {
    this.securityDetailDTO = securityDetailDTO;
    return this;
  }

   /**
   * Get securityDetailDTO
   * @return securityDetailDTO
  **/
  @ApiModelProperty(value = "")
  public SecurityDetailDTO getSecurityDetailDTO() {
    return securityDetailDTO;
  }

  public void setSecurityDetailDTO(SecurityDetailDTO securityDetailDTO) {
    this.securityDetailDTO = securityDetailDTO;
  }

  public RecallDTO securityId(Long securityId) {
    this.securityId = securityId;
    return this;
  }

   /**
   * Get securityId
   * @return securityId
  **/
  @ApiModelProperty(value = "")
  public Long getSecurityId() {
    return securityId;
  }

  public void setSecurityId(Long securityId) {
    this.securityId = securityId;
  }

  public RecallDTO settleDate(LocalDateTime settleDate) {
    this.settleDate = settleDate;
    return this;
  }

   /**
   * Get settleDate
   * @return settleDate
  **/
  @ApiModelProperty(value = "")
  public LocalDateTime getSettleDate() {
    return settleDate;
  }

  public void setSettleDate(LocalDateTime settleDate) {
    this.settleDate = settleDate;
  }

  public RecallDTO statusDTO(StatusDTO statusDTO) {
    this.statusDTO = statusDTO;
    return this;
  }

   /**
   * Get statusDTO
   * @return statusDTO
  **/
  @ApiModelProperty(value = "")
  public StatusDTO getStatusDTO() {
    return statusDTO;
  }

  public void setStatusDTO(StatusDTO statusDTO) {
    this.statusDTO = statusDTO;
  }

  public RecallDTO subAccountId(Long subAccountId) {
    this.subAccountId = subAccountId;
    return this;
  }

   /**
   * Get subAccountId
   * @return subAccountId
  **/
  @ApiModelProperty(value = "")
  public Long getSubAccountId() {
    return subAccountId;
  }

  public void setSubAccountId(Long subAccountId) {
    this.subAccountId = subAccountId;
  }

  public RecallDTO systemRefId(Integer systemRefId) {
    this.systemRefId = systemRefId;
    return this;
  }

   /**
   * Get systemRefId
   * @return systemRefId
  **/
  @ApiModelProperty(value = "")
  public Integer getSystemRefId() {
    return systemRefId;
  }

  public void setSystemRefId(Integer systemRefId) {
    this.systemRefId = systemRefId;
  }

  public RecallDTO tradeDate(LocalDateTime tradeDate) {
    this.tradeDate = tradeDate;
    return this;
  }

   /**
   * Get tradeDate
   * @return tradeDate
  **/
  @ApiModelProperty(value = "")
  public LocalDateTime getTradeDate() {
    return tradeDate;
  }

  public void setTradeDate(LocalDateTime tradeDate) {
    this.tradeDate = tradeDate;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecallDTO recallDTO = (RecallDTO) o;
    return Objects.equals(this.accountAllocationDTOs, recallDTO.accountAllocationDTOs) &&
        Objects.equals(this.accountDTO, recallDTO.accountDTO) &&
        Objects.equals(this.accountId, recallDTO.accountId) &&
        Objects.equals(this.caller, recallDTO.caller) &&
        Objects.equals(this.comments, recallDTO.comments) &&
        Objects.equals(this.counterpartyId, recallDTO.counterpartyId) &&
        Objects.equals(this.createTs, recallDTO.createTs) &&
        Objects.equals(this.createUserId, recallDTO.createUserId) &&
        Objects.equals(this.depoId, recallDTO.depoId) &&
        Objects.equals(this.isCaller, recallDTO.isCaller) &&
        Objects.equals(this.lastModTs, recallDTO.lastModTs) &&
        Objects.equals(this.lastModUserId, recallDTO.lastModUserId) &&
        Objects.equals(this.lendingAgentId, recallDTO.lendingAgentId) &&
        Objects.equals(this.poolingAccountId, recallDTO.poolingAccountId) &&
        Objects.equals(this.positionId, recallDTO.positionId) &&
        Objects.equals(this.positionRef, recallDTO.positionRef) &&
        Objects.equals(this.rateChange, recallDTO.rateChange) &&
        Objects.equals(this.rateChangeIndex, recallDTO.rateChangeIndex) &&
        Objects.equals(this.rateChangeIndexId, recallDTO.rateChangeIndexId) &&
        Objects.equals(this.rateChangeRate, recallDTO.rateChangeRate) &&
        Objects.equals(this.rateChangeSpread, recallDTO.rateChangeSpread) &&
        Objects.equals(this.recallDate, recallDTO.recallDate) &&
        Objects.equals(this.recallDueDate, recallDTO.recallDueDate) &&
        Objects.equals(this.recallId, recallDTO.recallId) &&
        Objects.equals(this.recallPositionDTO, recallDTO.recallPositionDTO) &&
        Objects.equals(this.recallPositionDTOs, recallDTO.recallPositionDTOs) &&
        Objects.equals(this.recallReason, recallDTO.recallReason) &&
        Objects.equals(this.recallReasonId, recallDTO.recallReasonId) &&
        Objects.equals(this.recallStatus, recallDTO.recallStatus) &&
        Objects.equals(this.recallStatusId, recallDTO.recallStatusId) &&
        Objects.equals(this.recalled, recallDTO.recalled) &&
        Objects.equals(this.restrictInventory, recallDTO.restrictInventory) &&
        Objects.equals(this.saleQuantity, recallDTO.saleQuantity) &&
        Objects.equals(this.securityDetailDTO, recallDTO.securityDetailDTO) &&
        Objects.equals(this.securityId, recallDTO.securityId) &&
        Objects.equals(this.settleDate, recallDTO.settleDate) &&
        Objects.equals(this.statusDTO, recallDTO.statusDTO) &&
        Objects.equals(this.subAccountId, recallDTO.subAccountId) &&
        Objects.equals(this.systemRefId, recallDTO.systemRefId) &&
        Objects.equals(this.tradeDate, recallDTO.tradeDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountAllocationDTOs, accountDTO, accountId, caller, comments, counterpartyId, createTs, createUserId, depoId, isCaller, lastModTs, lastModUserId, lendingAgentId, poolingAccountId, positionId, positionRef, rateChange, rateChangeIndex, rateChangeIndexId, rateChangeRate, rateChangeSpread, recallDate, recallDueDate, recallId, recallPositionDTO, recallPositionDTOs, recallReason, recallReasonId, recallStatus, recallStatusId, recalled, restrictInventory, saleQuantity, securityDetailDTO, securityId, settleDate, statusDTO, subAccountId, systemRefId, tradeDate);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RecallDTO {\n");
    
    sb.append("    accountAllocationDTOs: ").append(toIndentedString(accountAllocationDTOs)).append("\n");
    sb.append("    accountDTO: ").append(toIndentedString(accountDTO)).append("\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    caller: ").append(toIndentedString(caller)).append("\n");
    sb.append("    comments: ").append(toIndentedString(comments)).append("\n");
    sb.append("    counterpartyId: ").append(toIndentedString(counterpartyId)).append("\n");
    sb.append("    createTs: ").append(toIndentedString(createTs)).append("\n");
    sb.append("    createUserId: ").append(toIndentedString(createUserId)).append("\n");
    sb.append("    depoId: ").append(toIndentedString(depoId)).append("\n");
    sb.append("    isCaller: ").append(toIndentedString(isCaller)).append("\n");
    sb.append("    lastModTs: ").append(toIndentedString(lastModTs)).append("\n");
    sb.append("    lastModUserId: ").append(toIndentedString(lastModUserId)).append("\n");
    sb.append("    lendingAgentId: ").append(toIndentedString(lendingAgentId)).append("\n");
    sb.append("    poolingAccountId: ").append(toIndentedString(poolingAccountId)).append("\n");
    sb.append("    positionId: ").append(toIndentedString(positionId)).append("\n");
    sb.append("    positionRef: ").append(toIndentedString(positionRef)).append("\n");
    sb.append("    rateChange: ").append(toIndentedString(rateChange)).append("\n");
    sb.append("    rateChangeIndex: ").append(toIndentedString(rateChangeIndex)).append("\n");
    sb.append("    rateChangeIndexId: ").append(toIndentedString(rateChangeIndexId)).append("\n");
    sb.append("    rateChangeRate: ").append(toIndentedString(rateChangeRate)).append("\n");
    sb.append("    rateChangeSpread: ").append(toIndentedString(rateChangeSpread)).append("\n");
    sb.append("    recallDate: ").append(toIndentedString(recallDate)).append("\n");
    sb.append("    recallDueDate: ").append(toIndentedString(recallDueDate)).append("\n");
    sb.append("    recallId: ").append(toIndentedString(recallId)).append("\n");
    sb.append("    recallPositionDTO: ").append(toIndentedString(recallPositionDTO)).append("\n");
    sb.append("    recallPositionDTOs: ").append(toIndentedString(recallPositionDTOs)).append("\n");
    sb.append("    recallReason: ").append(toIndentedString(recallReason)).append("\n");
    sb.append("    recallReasonId: ").append(toIndentedString(recallReasonId)).append("\n");
    sb.append("    recallStatus: ").append(toIndentedString(recallStatus)).append("\n");
    sb.append("    recallStatusId: ").append(toIndentedString(recallStatusId)).append("\n");
    sb.append("    recalled: ").append(toIndentedString(recalled)).append("\n");
    sb.append("    restrictInventory: ").append(toIndentedString(restrictInventory)).append("\n");
    sb.append("    saleQuantity: ").append(toIndentedString(saleQuantity)).append("\n");
    sb.append("    securityDetailDTO: ").append(toIndentedString(securityDetailDTO)).append("\n");
    sb.append("    securityId: ").append(toIndentedString(securityId)).append("\n");
    sb.append("    settleDate: ").append(toIndentedString(settleDate)).append("\n");
    sb.append("    statusDTO: ").append(toIndentedString(statusDTO)).append("\n");
    sb.append("    subAccountId: ").append(toIndentedString(subAccountId)).append("\n");
    sb.append("    systemRefId: ").append(toIndentedString(systemRefId)).append("\n");
    sb.append("    tradeDate: ").append(toIndentedString(tradeDate)).append("\n");
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

