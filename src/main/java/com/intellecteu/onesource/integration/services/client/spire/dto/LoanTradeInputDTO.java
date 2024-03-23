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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * LoanTradeInputDTO
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-03-23T11:29:17.054Z")
public class LoanTradeInputDTO {
  @JsonProperty("accrualAmount")
  private Double accrualAmount = null;

  @JsonProperty("accrualTypeId")
  private Integer accrualTypeId = null;

  @JsonProperty("action")
  private String action = null;

  @JsonProperty("allowTradeForNoCurrDaySecPrice")
  private Boolean allowTradeForNoCurrDaySecPrice = null;

  @JsonProperty("asOfEndDate")
  private LocalDateTime asOfEndDate = null;

  @JsonProperty("asOfStartDate")
  private LocalDateTime asOfStartDate = null;

  @JsonProperty("autoRecall")
  private Boolean autoRecall = null;

  @JsonProperty("autoReturn")
  private Boolean autoReturn = null;

  @JsonProperty("avpoPostId")
  private Integer avpoPostId = null;

  @JsonProperty("byPassLimits")
  private Boolean byPassLimits = null;

  @JsonProperty("bypassRTI")
  private Boolean bypassRTI = null;

  @JsonProperty("cancelFail")
  private String cancelFail = null;

  @JsonProperty("cancelMarks")
  private Boolean cancelMarks = null;

  @JsonProperty("chainedTradeDTO")
  private List<TradeDTO> chainedTradeDTO = null;

  @JsonProperty("checkCompliance")
  private Boolean checkCompliance = null;

  @JsonProperty("checkInventory")
  private Boolean checkInventory = null;

  @JsonProperty("checkLimits")
  private Boolean checkLimits = null;

  @JsonProperty("copyRestrictions")
  private Boolean copyRestrictions = null;

  @JsonProperty("createTerminationTrade")
  private Boolean createTerminationTrade = null;

  @JsonProperty("doNotCancelLinked")
  private Boolean doNotCancelLinked = null;

  @JsonProperty("doNotPost")
  private Boolean doNotPost = null;

  @JsonProperty("doNotRecall")
  private Boolean doNotRecall = null;

  @JsonProperty("endDate")
  private LocalDateTime endDate = null;

  @JsonProperty("errorMessage")
  private String errorMessage = null;

  @JsonProperty("eventList")
  private List<TradeEvent> eventList = null;

  @JsonProperty("groupedPositionRef")
  private String groupedPositionRef = null;

  @JsonProperty("includeCouponInterest")
  private Boolean includeCouponInterest = null;

  @JsonProperty("msgId")
  private Long msgId = null;

  @JsonProperty("orderId")
  private Long orderId = null;

  @JsonProperty("originalTrade")
  private TradeDTO originalTrade = null;

  @JsonProperty("position")
  private LoanDTO position = null;

  @JsonProperty("positionLimitUsed")
  private List<PositionlimitusedDTO> positionLimitUsed = null;

  @JsonProperty("postOrSettle")
  private Boolean postOrSettle = null;

  @JsonProperty("preTradeId")
  private Long preTradeId = null;

  @JsonProperty("skipAldCheck")
  private Boolean skipAldCheck = null;

  @JsonProperty("skipInventoryCheckAndUpdate")
  private Boolean skipInventoryCheckAndUpdate = null;

  @JsonProperty("skipLimitCheckAndUpdate")
  private Boolean skipLimitCheckAndUpdate = null;

  @JsonProperty("skipMinSpreadCheck")
  private Boolean skipMinSpreadCheck = null;

  @JsonProperty("skipOneSource")
  private Boolean skipOneSource = null;

  @JsonProperty("skipSecChillCheck")
  private Boolean skipSecChillCheck = null;

  @JsonProperty("source")
  private String source = null;

  @JsonProperty("startDate")
  private LocalDateTime startDate = null;

  @JsonProperty("system")
  private String system = null;

  @JsonProperty("systemId")
  private Integer systemId = null;

  @JsonProperty("trade")
  private TradeDTO trade = null;

  @JsonProperty("tradeRefNo")
  private String tradeRefNo = null;

  @JsonProperty("updateInventory")
  private Boolean updateInventory = null;

  @JsonProperty("userId")
  private Integer userId = null;

  @JsonProperty("userName")
  private String userName = null;

  public LoanTradeInputDTO accrualAmount(Double accrualAmount) {
    this.accrualAmount = accrualAmount;
    return this;
  }

   /**
   * Get accrualAmount
   * @return accrualAmount
  **/
  @ApiModelProperty(value = "")
  public Double getAccrualAmount() {
    return accrualAmount;
  }

  public void setAccrualAmount(Double accrualAmount) {
    this.accrualAmount = accrualAmount;
  }

  public LoanTradeInputDTO accrualTypeId(Integer accrualTypeId) {
    this.accrualTypeId = accrualTypeId;
    return this;
  }

   /**
   * Get accrualTypeId
   * @return accrualTypeId
  **/
  @ApiModelProperty(value = "")
  public Integer getAccrualTypeId() {
    return accrualTypeId;
  }

  public void setAccrualTypeId(Integer accrualTypeId) {
    this.accrualTypeId = accrualTypeId;
  }

  public LoanTradeInputDTO action(String action) {
    this.action = action;
    return this;
  }

   /**
   * Should be &#39;Cancel Loan Trade&#39; to Cancel Loan, &#39;Cancel Pay To Hold Trade&#39; to Cancel Pay To Hold, &#39;Adjust Rate&#39; to Re-rate on Loan and Pay To Hold, &#39;Decrease Pay To Hold&#39; to descrease Pay To Hold Qty, &#39;Increase Pay To Hold&#39; to increase Pay To Hold Qty
   * @return action
  **/
  @ApiModelProperty(required = true, value = "Should be 'Cancel Loan Trade' to Cancel Loan, 'Cancel Pay To Hold Trade' to Cancel Pay To Hold, 'Adjust Rate' to Re-rate on Loan and Pay To Hold, 'Decrease Pay To Hold' to descrease Pay To Hold Qty, 'Increase Pay To Hold' to increase Pay To Hold Qty")
  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public LoanTradeInputDTO allowTradeForNoCurrDaySecPrice(Boolean allowTradeForNoCurrDaySecPrice) {
    this.allowTradeForNoCurrDaySecPrice = allowTradeForNoCurrDaySecPrice;
    return this;
  }

   /**
   * Get allowTradeForNoCurrDaySecPrice
   * @return allowTradeForNoCurrDaySecPrice
  **/
  @ApiModelProperty(value = "")
  public Boolean isAllowTradeForNoCurrDaySecPrice() {
    return allowTradeForNoCurrDaySecPrice;
  }

  public void setAllowTradeForNoCurrDaySecPrice(Boolean allowTradeForNoCurrDaySecPrice) {
    this.allowTradeForNoCurrDaySecPrice = allowTradeForNoCurrDaySecPrice;
  }

  public LoanTradeInputDTO asOfEndDate(LocalDateTime asOfEndDate) {
    this.asOfEndDate = asOfEndDate;
    return this;
  }

   /**
   * Get asOfEndDate
   * @return asOfEndDate
  **/
  @ApiModelProperty(value = "")
  public LocalDateTime getAsOfEndDate() {
    return asOfEndDate;
  }

  public void setAsOfEndDate(LocalDateTime asOfEndDate) {
    this.asOfEndDate = asOfEndDate;
  }

  public LoanTradeInputDTO asOfStartDate(LocalDateTime asOfStartDate) {
    this.asOfStartDate = asOfStartDate;
    return this;
  }

   /**
   * Get asOfStartDate
   * @return asOfStartDate
  **/
  @ApiModelProperty(value = "")
  public LocalDateTime getAsOfStartDate() {
    return asOfStartDate;
  }

  public void setAsOfStartDate(LocalDateTime asOfStartDate) {
    this.asOfStartDate = asOfStartDate;
  }

  public LoanTradeInputDTO autoRecall(Boolean autoRecall) {
    this.autoRecall = autoRecall;
    return this;
  }

   /**
   * Get autoRecall
   * @return autoRecall
  **/
  @ApiModelProperty(value = "")
  public Boolean isAutoRecall() {
    return autoRecall;
  }

  public void setAutoRecall(Boolean autoRecall) {
    this.autoRecall = autoRecall;
  }

  public LoanTradeInputDTO autoReturn(Boolean autoReturn) {
    this.autoReturn = autoReturn;
    return this;
  }

   /**
   * Get autoReturn
   * @return autoReturn
  **/
  @ApiModelProperty(value = "")
  public Boolean isAutoReturn() {
    return autoReturn;
  }

  public void setAutoReturn(Boolean autoReturn) {
    this.autoReturn = autoReturn;
  }

  public LoanTradeInputDTO avpoPostId(Integer avpoPostId) {
    this.avpoPostId = avpoPostId;
    return this;
  }

   /**
   * Get avpoPostId
   * @return avpoPostId
  **/
  @ApiModelProperty(value = "")
  public Integer getAvpoPostId() {
    return avpoPostId;
  }

  public void setAvpoPostId(Integer avpoPostId) {
    this.avpoPostId = avpoPostId;
  }

  public LoanTradeInputDTO byPassLimits(Boolean byPassLimits) {
    this.byPassLimits = byPassLimits;
    return this;
  }

   /**
   * Get byPassLimits
   * @return byPassLimits
  **/
  @ApiModelProperty(value = "")
  public Boolean isByPassLimits() {
    return byPassLimits;
  }

  public void setByPassLimits(Boolean byPassLimits) {
    this.byPassLimits = byPassLimits;
  }

  public LoanTradeInputDTO bypassRTI(Boolean bypassRTI) {
    this.bypassRTI = bypassRTI;
    return this;
  }

   /**
   * Get bypassRTI
   * @return bypassRTI
  **/
  @ApiModelProperty(value = "")
  public Boolean isBypassRTI() {
    return bypassRTI;
  }

  public void setBypassRTI(Boolean bypassRTI) {
    this.bypassRTI = bypassRTI;
  }

  public LoanTradeInputDTO cancelFail(String cancelFail) {
    this.cancelFail = cancelFail;
    return this;
  }

   /**
   * Get cancelFail
   * @return cancelFail
  **/
  @ApiModelProperty(value = "")
  public String getCancelFail() {
    return cancelFail;
  }

  public void setCancelFail(String cancelFail) {
    this.cancelFail = cancelFail;
  }

  public LoanTradeInputDTO cancelMarks(Boolean cancelMarks) {
    this.cancelMarks = cancelMarks;
    return this;
  }

   /**
   * Get cancelMarks
   * @return cancelMarks
  **/
  @ApiModelProperty(value = "")
  public Boolean isCancelMarks() {
    return cancelMarks;
  }

  public void setCancelMarks(Boolean cancelMarks) {
    this.cancelMarks = cancelMarks;
  }

  public LoanTradeInputDTO chainedTradeDTO(List<TradeDTO> chainedTradeDTO) {
    this.chainedTradeDTO = chainedTradeDTO;
    return this;
  }

  public LoanTradeInputDTO addChainedTradeDTOItem(TradeDTO chainedTradeDTOItem) {
    if (this.chainedTradeDTO == null) {
      this.chainedTradeDTO = new ArrayList<>();
    }
    this.chainedTradeDTO.add(chainedTradeDTOItem);
    return this;
  }

   /**
   * Get chainedTradeDTO
   * @return chainedTradeDTO
  **/
  @ApiModelProperty(value = "")
  public List<TradeDTO> getChainedTradeDTO() {
    return chainedTradeDTO;
  }

  public void setChainedTradeDTO(List<TradeDTO> chainedTradeDTO) {
    this.chainedTradeDTO = chainedTradeDTO;
  }

  public LoanTradeInputDTO checkCompliance(Boolean checkCompliance) {
    this.checkCompliance = checkCompliance;
    return this;
  }

   /**
   * Get checkCompliance
   * @return checkCompliance
  **/
  @ApiModelProperty(value = "")
  public Boolean isCheckCompliance() {
    return checkCompliance;
  }

  public void setCheckCompliance(Boolean checkCompliance) {
    this.checkCompliance = checkCompliance;
  }

  public LoanTradeInputDTO checkInventory(Boolean checkInventory) {
    this.checkInventory = checkInventory;
    return this;
  }

   /**
   * Get checkInventory
   * @return checkInventory
  **/
  @ApiModelProperty(value = "")
  public Boolean isCheckInventory() {
    return checkInventory;
  }

  public void setCheckInventory(Boolean checkInventory) {
    this.checkInventory = checkInventory;
  }

  public LoanTradeInputDTO checkLimits(Boolean checkLimits) {
    this.checkLimits = checkLimits;
    return this;
  }

   /**
   * Get checkLimits
   * @return checkLimits
  **/
  @ApiModelProperty(value = "")
  public Boolean isCheckLimits() {
    return checkLimits;
  }

  public void setCheckLimits(Boolean checkLimits) {
    this.checkLimits = checkLimits;
  }

  public LoanTradeInputDTO copyRestrictions(Boolean copyRestrictions) {
    this.copyRestrictions = copyRestrictions;
    return this;
  }

   /**
   * Get copyRestrictions
   * @return copyRestrictions
  **/
  @ApiModelProperty(value = "")
  public Boolean isCopyRestrictions() {
    return copyRestrictions;
  }

  public void setCopyRestrictions(Boolean copyRestrictions) {
    this.copyRestrictions = copyRestrictions;
  }

  public LoanTradeInputDTO createTerminationTrade(Boolean createTerminationTrade) {
    this.createTerminationTrade = createTerminationTrade;
    return this;
  }

   /**
   * Get createTerminationTrade
   * @return createTerminationTrade
  **/
  @ApiModelProperty(value = "")
  public Boolean isCreateTerminationTrade() {
    return createTerminationTrade;
  }

  public void setCreateTerminationTrade(Boolean createTerminationTrade) {
    this.createTerminationTrade = createTerminationTrade;
  }

  public LoanTradeInputDTO doNotCancelLinked(Boolean doNotCancelLinked) {
    this.doNotCancelLinked = doNotCancelLinked;
    return this;
  }

   /**
   * Get doNotCancelLinked
   * @return doNotCancelLinked
  **/
  @ApiModelProperty(value = "")
  public Boolean isDoNotCancelLinked() {
    return doNotCancelLinked;
  }

  public void setDoNotCancelLinked(Boolean doNotCancelLinked) {
    this.doNotCancelLinked = doNotCancelLinked;
  }

  public LoanTradeInputDTO doNotPost(Boolean doNotPost) {
    this.doNotPost = doNotPost;
    return this;
  }

   /**
   * Get doNotPost
   * @return doNotPost
  **/
  @ApiModelProperty(value = "")
  public Boolean isDoNotPost() {
    return doNotPost;
  }

  public void setDoNotPost(Boolean doNotPost) {
    this.doNotPost = doNotPost;
  }

  public LoanTradeInputDTO doNotRecall(Boolean doNotRecall) {
    this.doNotRecall = doNotRecall;
    return this;
  }

   /**
   * Get doNotRecall
   * @return doNotRecall
  **/
  @ApiModelProperty(value = "")
  public Boolean isDoNotRecall() {
    return doNotRecall;
  }

  public void setDoNotRecall(Boolean doNotRecall) {
    this.doNotRecall = doNotRecall;
  }

  public LoanTradeInputDTO endDate(LocalDateTime endDate) {
    this.endDate = endDate;
    return this;
  }

   /**
   * Required only when adjusting rate
   * @return endDate
  **/
  @ApiModelProperty(required = true, value = "Required only when adjusting rate")
  public LocalDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public LoanTradeInputDTO errorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

   /**
   * Get errorMessage
   * @return errorMessage
  **/
  @ApiModelProperty(value = "")
  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public LoanTradeInputDTO eventList(List<TradeEvent> eventList) {
    this.eventList = eventList;
    return this;
  }

  public LoanTradeInputDTO addEventListItem(TradeEvent eventListItem) {
    if (this.eventList == null) {
      this.eventList = new ArrayList<>();
    }
    this.eventList.add(eventListItem);
    return this;
  }

   /**
   * Get eventList
   * @return eventList
  **/
  @ApiModelProperty(value = "")
  public List<TradeEvent> getEventList() {
    return eventList;
  }

  public void setEventList(List<TradeEvent> eventList) {
    this.eventList = eventList;
  }

  public LoanTradeInputDTO groupedPositionRef(String groupedPositionRef) {
    this.groupedPositionRef = groupedPositionRef;
    return this;
  }

   /**
   * Get groupedPositionRef
   * @return groupedPositionRef
  **/
  @ApiModelProperty(value = "")
  public String getGroupedPositionRef() {
    return groupedPositionRef;
  }

  public void setGroupedPositionRef(String groupedPositionRef) {
    this.groupedPositionRef = groupedPositionRef;
  }

  public LoanTradeInputDTO includeCouponInterest(Boolean includeCouponInterest) {
    this.includeCouponInterest = includeCouponInterest;
    return this;
  }

   /**
   * Get includeCouponInterest
   * @return includeCouponInterest
  **/
  @ApiModelProperty(value = "")
  public Boolean isIncludeCouponInterest() {
    return includeCouponInterest;
  }

  public void setIncludeCouponInterest(Boolean includeCouponInterest) {
    this.includeCouponInterest = includeCouponInterest;
  }

  public LoanTradeInputDTO msgId(Long msgId) {
    this.msgId = msgId;
    return this;
  }

   /**
   * Get msgId
   * @return msgId
  **/
  @ApiModelProperty(value = "")
  public Long getMsgId() {
    return msgId;
  }

  public void setMsgId(Long msgId) {
    this.msgId = msgId;
  }

  public LoanTradeInputDTO orderId(Long orderId) {
    this.orderId = orderId;
    return this;
  }

   /**
   * Get orderId
   * @return orderId
  **/
  @ApiModelProperty(value = "")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public LoanTradeInputDTO originalTrade(TradeDTO originalTrade) {
    this.originalTrade = originalTrade;
    return this;
  }

   /**
   * Get originalTrade
   * @return originalTrade
  **/
  @ApiModelProperty(value = "")
  public TradeDTO getOriginalTrade() {
    return originalTrade;
  }

  public void setOriginalTrade(TradeDTO originalTrade) {
    this.originalTrade = originalTrade;
  }

  public LoanTradeInputDTO position(LoanDTO position) {
    this.position = position;
    return this;
  }

   /**
   * Get position
   * @return position
  **/
  @ApiModelProperty(value = "")
  public LoanDTO getPosition() {
    return position;
  }

  public void setPosition(LoanDTO position) {
    this.position = position;
  }

  public LoanTradeInputDTO positionLimitUsed(List<PositionlimitusedDTO> positionLimitUsed) {
    this.positionLimitUsed = positionLimitUsed;
    return this;
  }

  public LoanTradeInputDTO addPositionLimitUsedItem(PositionlimitusedDTO positionLimitUsedItem) {
    if (this.positionLimitUsed == null) {
      this.positionLimitUsed = new ArrayList<>();
    }
    this.positionLimitUsed.add(positionLimitUsedItem);
    return this;
  }

   /**
   * Get positionLimitUsed
   * @return positionLimitUsed
  **/
  @ApiModelProperty(value = "")
  public List<PositionlimitusedDTO> getPositionLimitUsed() {
    return positionLimitUsed;
  }

  public void setPositionLimitUsed(List<PositionlimitusedDTO> positionLimitUsed) {
    this.positionLimitUsed = positionLimitUsed;
  }

  public LoanTradeInputDTO postOrSettle(Boolean postOrSettle) {
    this.postOrSettle = postOrSettle;
    return this;
  }

   /**
   * Get postOrSettle
   * @return postOrSettle
  **/
  @ApiModelProperty(value = "")
  public Boolean isPostOrSettle() {
    return postOrSettle;
  }

  public void setPostOrSettle(Boolean postOrSettle) {
    this.postOrSettle = postOrSettle;
  }

  public LoanTradeInputDTO preTradeId(Long preTradeId) {
    this.preTradeId = preTradeId;
    return this;
  }

   /**
   * Get preTradeId
   * @return preTradeId
  **/
  @ApiModelProperty(value = "")
  public Long getPreTradeId() {
    return preTradeId;
  }

  public void setPreTradeId(Long preTradeId) {
    this.preTradeId = preTradeId;
  }

  public LoanTradeInputDTO skipAldCheck(Boolean skipAldCheck) {
    this.skipAldCheck = skipAldCheck;
    return this;
  }

   /**
   * Get skipAldCheck
   * @return skipAldCheck
  **/
  @ApiModelProperty(value = "")
  public Boolean isSkipAldCheck() {
    return skipAldCheck;
  }

  public void setSkipAldCheck(Boolean skipAldCheck) {
    this.skipAldCheck = skipAldCheck;
  }

  public LoanTradeInputDTO skipInventoryCheckAndUpdate(Boolean skipInventoryCheckAndUpdate) {
    this.skipInventoryCheckAndUpdate = skipInventoryCheckAndUpdate;
    return this;
  }

   /**
   * Get skipInventoryCheckAndUpdate
   * @return skipInventoryCheckAndUpdate
  **/
  @ApiModelProperty(value = "")
  public Boolean isSkipInventoryCheckAndUpdate() {
    return skipInventoryCheckAndUpdate;
  }

  public void setSkipInventoryCheckAndUpdate(Boolean skipInventoryCheckAndUpdate) {
    this.skipInventoryCheckAndUpdate = skipInventoryCheckAndUpdate;
  }

  public LoanTradeInputDTO skipLimitCheckAndUpdate(Boolean skipLimitCheckAndUpdate) {
    this.skipLimitCheckAndUpdate = skipLimitCheckAndUpdate;
    return this;
  }

   /**
   * Get skipLimitCheckAndUpdate
   * @return skipLimitCheckAndUpdate
  **/
  @ApiModelProperty(value = "")
  public Boolean isSkipLimitCheckAndUpdate() {
    return skipLimitCheckAndUpdate;
  }

  public void setSkipLimitCheckAndUpdate(Boolean skipLimitCheckAndUpdate) {
    this.skipLimitCheckAndUpdate = skipLimitCheckAndUpdate;
  }

  public LoanTradeInputDTO skipMinSpreadCheck(Boolean skipMinSpreadCheck) {
    this.skipMinSpreadCheck = skipMinSpreadCheck;
    return this;
  }

   /**
   * Get skipMinSpreadCheck
   * @return skipMinSpreadCheck
  **/
  @ApiModelProperty(value = "")
  public Boolean isSkipMinSpreadCheck() {
    return skipMinSpreadCheck;
  }

  public void setSkipMinSpreadCheck(Boolean skipMinSpreadCheck) {
    this.skipMinSpreadCheck = skipMinSpreadCheck;
  }

  public LoanTradeInputDTO skipOneSource(Boolean skipOneSource) {
    this.skipOneSource = skipOneSource;
    return this;
  }

   /**
   * Get skipOneSource
   * @return skipOneSource
  **/
  @ApiModelProperty(value = "")
  public Boolean isSkipOneSource() {
    return skipOneSource;
  }

  public void setSkipOneSource(Boolean skipOneSource) {
    this.skipOneSource = skipOneSource;
  }

  public LoanTradeInputDTO skipSecChillCheck(Boolean skipSecChillCheck) {
    this.skipSecChillCheck = skipSecChillCheck;
    return this;
  }

   /**
   * Get skipSecChillCheck
   * @return skipSecChillCheck
  **/
  @ApiModelProperty(value = "")
  public Boolean isSkipSecChillCheck() {
    return skipSecChillCheck;
  }

  public void setSkipSecChillCheck(Boolean skipSecChillCheck) {
    this.skipSecChillCheck = skipSecChillCheck;
  }

  public LoanTradeInputDTO source(String source) {
    this.source = source;
    return this;
  }

   /**
   * Get source
   * @return source
  **/
  @ApiModelProperty(value = "")
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public LoanTradeInputDTO startDate(LocalDateTime startDate) {
    this.startDate = startDate;
    return this;
  }

   /**
   * Required only when adjusting rate
   * @return startDate
  **/
  @ApiModelProperty(required = true, value = "Required only when adjusting rate")
  public LocalDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public LoanTradeInputDTO system(String system) {
    this.system = system;
    return this;
  }

   /**
   * Get system
   * @return system
  **/
  @ApiModelProperty(value = "")
  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public LoanTradeInputDTO systemId(Integer systemId) {
    this.systemId = systemId;
    return this;
  }

   /**
   * Get systemId
   * @return systemId
  **/
  @ApiModelProperty(value = "")
  public Integer getSystemId() {
    return systemId;
  }

  public void setSystemId(Integer systemId) {
    this.systemId = systemId;
  }

  public LoanTradeInputDTO trade(TradeDTO trade) {
    this.trade = trade;
    return this;
  }

   /**
   * Get trade
   * @return trade
  **/
  @ApiModelProperty(value = "")
  public TradeDTO getTrade() {
    return trade;
  }

  public void setTrade(TradeDTO trade) {
    this.trade = trade;
  }

  public LoanTradeInputDTO tradeRefNo(String tradeRefNo) {
    this.tradeRefNo = tradeRefNo;
    return this;
  }

   /**
   * Get tradeRefNo
   * @return tradeRefNo
  **/
  @ApiModelProperty(value = "")
  public String getTradeRefNo() {
    return tradeRefNo;
  }

  public void setTradeRefNo(String tradeRefNo) {
    this.tradeRefNo = tradeRefNo;
  }

  public LoanTradeInputDTO updateInventory(Boolean updateInventory) {
    this.updateInventory = updateInventory;
    return this;
  }

   /**
   * Get updateInventory
   * @return updateInventory
  **/
  @ApiModelProperty(value = "")
  public Boolean isUpdateInventory() {
    return updateInventory;
  }

  public void setUpdateInventory(Boolean updateInventory) {
    this.updateInventory = updateInventory;
  }

  public LoanTradeInputDTO userId(Integer userId) {
    this.userId = userId;
    return this;
  }

   /**
   * Either userId or userName is required
   * @return userId
  **/
  @ApiModelProperty(required = true, value = "Either userId or userName is required")
  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public LoanTradeInputDTO userName(String userName) {
    this.userName = userName;
    return this;
  }

   /**
   * Either userId or userName is required
   * @return userName
  **/
  @ApiModelProperty(required = true, value = "Either userId or userName is required")
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LoanTradeInputDTO loanTradeInputDTO = (LoanTradeInputDTO) o;
    return Objects.equals(this.accrualAmount, loanTradeInputDTO.accrualAmount) &&
        Objects.equals(this.accrualTypeId, loanTradeInputDTO.accrualTypeId) &&
        Objects.equals(this.action, loanTradeInputDTO.action) &&
        Objects.equals(this.allowTradeForNoCurrDaySecPrice, loanTradeInputDTO.allowTradeForNoCurrDaySecPrice) &&
        Objects.equals(this.asOfEndDate, loanTradeInputDTO.asOfEndDate) &&
        Objects.equals(this.asOfStartDate, loanTradeInputDTO.asOfStartDate) &&
        Objects.equals(this.autoRecall, loanTradeInputDTO.autoRecall) &&
        Objects.equals(this.autoReturn, loanTradeInputDTO.autoReturn) &&
        Objects.equals(this.avpoPostId, loanTradeInputDTO.avpoPostId) &&
        Objects.equals(this.byPassLimits, loanTradeInputDTO.byPassLimits) &&
        Objects.equals(this.bypassRTI, loanTradeInputDTO.bypassRTI) &&
        Objects.equals(this.cancelFail, loanTradeInputDTO.cancelFail) &&
        Objects.equals(this.cancelMarks, loanTradeInputDTO.cancelMarks) &&
        Objects.equals(this.chainedTradeDTO, loanTradeInputDTO.chainedTradeDTO) &&
        Objects.equals(this.checkCompliance, loanTradeInputDTO.checkCompliance) &&
        Objects.equals(this.checkInventory, loanTradeInputDTO.checkInventory) &&
        Objects.equals(this.checkLimits, loanTradeInputDTO.checkLimits) &&
        Objects.equals(this.copyRestrictions, loanTradeInputDTO.copyRestrictions) &&
        Objects.equals(this.createTerminationTrade, loanTradeInputDTO.createTerminationTrade) &&
        Objects.equals(this.doNotCancelLinked, loanTradeInputDTO.doNotCancelLinked) &&
        Objects.equals(this.doNotPost, loanTradeInputDTO.doNotPost) &&
        Objects.equals(this.doNotRecall, loanTradeInputDTO.doNotRecall) &&
        Objects.equals(this.endDate, loanTradeInputDTO.endDate) &&
        Objects.equals(this.errorMessage, loanTradeInputDTO.errorMessage) &&
        Objects.equals(this.eventList, loanTradeInputDTO.eventList) &&
        Objects.equals(this.groupedPositionRef, loanTradeInputDTO.groupedPositionRef) &&
        Objects.equals(this.includeCouponInterest, loanTradeInputDTO.includeCouponInterest) &&
        Objects.equals(this.msgId, loanTradeInputDTO.msgId) &&
        Objects.equals(this.orderId, loanTradeInputDTO.orderId) &&
        Objects.equals(this.originalTrade, loanTradeInputDTO.originalTrade) &&
        Objects.equals(this.position, loanTradeInputDTO.position) &&
        Objects.equals(this.positionLimitUsed, loanTradeInputDTO.positionLimitUsed) &&
        Objects.equals(this.postOrSettle, loanTradeInputDTO.postOrSettle) &&
        Objects.equals(this.preTradeId, loanTradeInputDTO.preTradeId) &&
        Objects.equals(this.skipAldCheck, loanTradeInputDTO.skipAldCheck) &&
        Objects.equals(this.skipInventoryCheckAndUpdate, loanTradeInputDTO.skipInventoryCheckAndUpdate) &&
        Objects.equals(this.skipLimitCheckAndUpdate, loanTradeInputDTO.skipLimitCheckAndUpdate) &&
        Objects.equals(this.skipMinSpreadCheck, loanTradeInputDTO.skipMinSpreadCheck) &&
        Objects.equals(this.skipOneSource, loanTradeInputDTO.skipOneSource) &&
        Objects.equals(this.skipSecChillCheck, loanTradeInputDTO.skipSecChillCheck) &&
        Objects.equals(this.source, loanTradeInputDTO.source) &&
        Objects.equals(this.startDate, loanTradeInputDTO.startDate) &&
        Objects.equals(this.system, loanTradeInputDTO.system) &&
        Objects.equals(this.systemId, loanTradeInputDTO.systemId) &&
        Objects.equals(this.trade, loanTradeInputDTO.trade) &&
        Objects.equals(this.tradeRefNo, loanTradeInputDTO.tradeRefNo) &&
        Objects.equals(this.updateInventory, loanTradeInputDTO.updateInventory) &&
        Objects.equals(this.userId, loanTradeInputDTO.userId) &&
        Objects.equals(this.userName, loanTradeInputDTO.userName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accrualAmount, accrualTypeId, action, allowTradeForNoCurrDaySecPrice, asOfEndDate, asOfStartDate, autoRecall, autoReturn, avpoPostId, byPassLimits, bypassRTI, cancelFail, cancelMarks, chainedTradeDTO, checkCompliance, checkInventory, checkLimits, copyRestrictions, createTerminationTrade, doNotCancelLinked, doNotPost, doNotRecall, endDate, errorMessage, eventList, groupedPositionRef, includeCouponInterest, msgId, orderId, originalTrade, position, positionLimitUsed, postOrSettle, preTradeId, skipAldCheck, skipInventoryCheckAndUpdate, skipLimitCheckAndUpdate, skipMinSpreadCheck, skipOneSource, skipSecChillCheck, source, startDate, system, systemId, trade, tradeRefNo, updateInventory, userId, userName);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoanTradeInputDTO {\n");
    
    sb.append("    accrualAmount: ").append(toIndentedString(accrualAmount)).append("\n");
    sb.append("    accrualTypeId: ").append(toIndentedString(accrualTypeId)).append("\n");
    sb.append("    action: ").append(toIndentedString(action)).append("\n");
    sb.append("    allowTradeForNoCurrDaySecPrice: ").append(toIndentedString(allowTradeForNoCurrDaySecPrice)).append("\n");
    sb.append("    asOfEndDate: ").append(toIndentedString(asOfEndDate)).append("\n");
    sb.append("    asOfStartDate: ").append(toIndentedString(asOfStartDate)).append("\n");
    sb.append("    autoRecall: ").append(toIndentedString(autoRecall)).append("\n");
    sb.append("    autoReturn: ").append(toIndentedString(autoReturn)).append("\n");
    sb.append("    avpoPostId: ").append(toIndentedString(avpoPostId)).append("\n");
    sb.append("    byPassLimits: ").append(toIndentedString(byPassLimits)).append("\n");
    sb.append("    bypassRTI: ").append(toIndentedString(bypassRTI)).append("\n");
    sb.append("    cancelFail: ").append(toIndentedString(cancelFail)).append("\n");
    sb.append("    cancelMarks: ").append(toIndentedString(cancelMarks)).append("\n");
    sb.append("    chainedTradeDTO: ").append(toIndentedString(chainedTradeDTO)).append("\n");
    sb.append("    checkCompliance: ").append(toIndentedString(checkCompliance)).append("\n");
    sb.append("    checkInventory: ").append(toIndentedString(checkInventory)).append("\n");
    sb.append("    checkLimits: ").append(toIndentedString(checkLimits)).append("\n");
    sb.append("    copyRestrictions: ").append(toIndentedString(copyRestrictions)).append("\n");
    sb.append("    createTerminationTrade: ").append(toIndentedString(createTerminationTrade)).append("\n");
    sb.append("    doNotCancelLinked: ").append(toIndentedString(doNotCancelLinked)).append("\n");
    sb.append("    doNotPost: ").append(toIndentedString(doNotPost)).append("\n");
    sb.append("    doNotRecall: ").append(toIndentedString(doNotRecall)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    errorMessage: ").append(toIndentedString(errorMessage)).append("\n");
    sb.append("    eventList: ").append(toIndentedString(eventList)).append("\n");
    sb.append("    groupedPositionRef: ").append(toIndentedString(groupedPositionRef)).append("\n");
    sb.append("    includeCouponInterest: ").append(toIndentedString(includeCouponInterest)).append("\n");
    sb.append("    msgId: ").append(toIndentedString(msgId)).append("\n");
    sb.append("    orderId: ").append(toIndentedString(orderId)).append("\n");
    sb.append("    originalTrade: ").append(toIndentedString(originalTrade)).append("\n");
    sb.append("    position: ").append(toIndentedString(position)).append("\n");
    sb.append("    positionLimitUsed: ").append(toIndentedString(positionLimitUsed)).append("\n");
    sb.append("    postOrSettle: ").append(toIndentedString(postOrSettle)).append("\n");
    sb.append("    preTradeId: ").append(toIndentedString(preTradeId)).append("\n");
    sb.append("    skipAldCheck: ").append(toIndentedString(skipAldCheck)).append("\n");
    sb.append("    skipInventoryCheckAndUpdate: ").append(toIndentedString(skipInventoryCheckAndUpdate)).append("\n");
    sb.append("    skipLimitCheckAndUpdate: ").append(toIndentedString(skipLimitCheckAndUpdate)).append("\n");
    sb.append("    skipMinSpreadCheck: ").append(toIndentedString(skipMinSpreadCheck)).append("\n");
    sb.append("    skipOneSource: ").append(toIndentedString(skipOneSource)).append("\n");
    sb.append("    skipSecChillCheck: ").append(toIndentedString(skipSecChillCheck)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    system: ").append(toIndentedString(system)).append("\n");
    sb.append("    systemId: ").append(toIndentedString(systemId)).append("\n");
    sb.append("    trade: ").append(toIndentedString(trade)).append("\n");
    sb.append("    tradeRefNo: ").append(toIndentedString(tradeRefNo)).append("\n");
    sb.append("    updateInventory: ").append(toIndentedString(updateInventory)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
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

