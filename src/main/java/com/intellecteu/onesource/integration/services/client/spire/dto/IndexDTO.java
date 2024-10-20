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
import java.util.Objects;

/**
 * IndexDTO
 */

public class IndexDTO {
  @JsonProperty("bmAccountId")
  private Long bmAccountId = null;

  @JsonProperty("calendarDTO")
  private CalendarDTO calendarDTO = null;

  @JsonProperty("calendarId")
  private Integer calendarId = null;

  @JsonProperty("counterPartyDTO")
  private AccountDTO counterPartyDTO = null;

  @JsonProperty("counterpartyGroupAccountNo")
  private String counterpartyGroupAccountNo = null;

  @JsonProperty("counterpartyGroupName")
  private String counterpartyGroupName = null;

  @JsonProperty("counterpartyGroupdtcNo")
  private String counterpartyGroupdtcNo = null;

  @JsonProperty("counterpartyId")
  private Long counterpartyId = null;

  @JsonProperty("counterpartydtcNo")
  private String counterpartydtcNo = null;

  @JsonProperty("eqlBenchmark")
  private String eqlBenchmark = null;

  @JsonProperty("extIndexRef")
  private String extIndexRef = null;

  @JsonProperty("indexCategory")
  private String indexCategory = null;

  @JsonProperty("indexId")
  private Long indexId = null;

  @JsonProperty("indexName")
  private String indexName = null;

  @JsonProperty("isActive")
  private Boolean isActive = null;

  @JsonProperty("lookBack")
  private Long lookBack = null;

  @JsonProperty("parentIndexId")
  private Long parentIndexId = null;

  @JsonProperty("parentIndexName")
  private String parentIndexName = null;

  @JsonProperty("securityDetailDTO")
  private SecurityDetailDTO securityDetailDTO = null;

  @JsonProperty("securityId")
  private Long securityId = null;

  @JsonProperty("sftrPmtFreqMulti")
  private Integer sftrPmtFreqMulti = null;

  @JsonProperty("sftrPmtFreqTime")
  private String sftrPmtFreqTime = null;

  @JsonProperty("sftrRateName")
  private String sftrRateName = null;

  @JsonProperty("sftrRefPerMulti")
  private Integer sftrRefPerMulti = null;

  @JsonProperty("sftrRefPerTime")
  private String sftrRefPerTime = null;

  @JsonProperty("sftrResetFreqMulti")
  private Integer sftrResetFreqMulti = null;

  @JsonProperty("sftrResetFreqTime")
  private String sftrResetFreqTime = null;

  @JsonProperty("spread")
  private Double spread = null;

  public IndexDTO bmAccountId(Long bmAccountId) {
    this.bmAccountId = bmAccountId;
    return this;
  }

   /**
   * Get bmAccountId
   * @return bmAccountId
  **/
  @ApiModelProperty(value = "")
  public Long getBmAccountId() {
    return bmAccountId;
  }

  public void setBmAccountId(Long bmAccountId) {
    this.bmAccountId = bmAccountId;
  }

  public IndexDTO calendarDTO(CalendarDTO calendarDTO) {
    this.calendarDTO = calendarDTO;
    return this;
  }

   /**
   * Get calendarDTO
   * @return calendarDTO
  **/
  @ApiModelProperty(value = "")
  public CalendarDTO getCalendarDTO() {
    return calendarDTO;
  }

  public void setCalendarDTO(CalendarDTO calendarDTO) {
    this.calendarDTO = calendarDTO;
  }

  public IndexDTO calendarId(Integer calendarId) {
    this.calendarId = calendarId;
    return this;
  }

   /**
   * Get calendarId
   * @return calendarId
  **/
  @ApiModelProperty(value = "")
  public Integer getCalendarId() {
    return calendarId;
  }

  public void setCalendarId(Integer calendarId) {
    this.calendarId = calendarId;
  }

  public IndexDTO counterPartyDTO(AccountDTO counterPartyDTO) {
    this.counterPartyDTO = counterPartyDTO;
    return this;
  }

   /**
   * Get counterPartyDTO
   * @return counterPartyDTO
  **/
  @ApiModelProperty(value = "")
  public AccountDTO getCounterPartyDTO() {
    return counterPartyDTO;
  }

  public void setCounterPartyDTO(AccountDTO counterPartyDTO) {
    this.counterPartyDTO = counterPartyDTO;
  }

  public IndexDTO counterpartyGroupAccountNo(String counterpartyGroupAccountNo) {
    this.counterpartyGroupAccountNo = counterpartyGroupAccountNo;
    return this;
  }

   /**
   * Get counterpartyGroupAccountNo
   * @return counterpartyGroupAccountNo
  **/
  @ApiModelProperty(value = "")
  public String getCounterpartyGroupAccountNo() {
    return counterpartyGroupAccountNo;
  }

  public void setCounterpartyGroupAccountNo(String counterpartyGroupAccountNo) {
    this.counterpartyGroupAccountNo = counterpartyGroupAccountNo;
  }

  public IndexDTO counterpartyGroupName(String counterpartyGroupName) {
    this.counterpartyGroupName = counterpartyGroupName;
    return this;
  }

   /**
   * Get counterpartyGroupName
   * @return counterpartyGroupName
  **/
  @ApiModelProperty(value = "")
  public String getCounterpartyGroupName() {
    return counterpartyGroupName;
  }

  public void setCounterpartyGroupName(String counterpartyGroupName) {
    this.counterpartyGroupName = counterpartyGroupName;
  }

  public IndexDTO counterpartyGroupdtcNo(String counterpartyGroupdtcNo) {
    this.counterpartyGroupdtcNo = counterpartyGroupdtcNo;
    return this;
  }

   /**
   * Get counterpartyGroupdtcNo
   * @return counterpartyGroupdtcNo
  **/
  @ApiModelProperty(value = "")
  public String getCounterpartyGroupdtcNo() {
    return counterpartyGroupdtcNo;
  }

  public void setCounterpartyGroupdtcNo(String counterpartyGroupdtcNo) {
    this.counterpartyGroupdtcNo = counterpartyGroupdtcNo;
  }

  public IndexDTO counterpartyId(Long counterpartyId) {
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

  public IndexDTO counterpartydtcNo(String counterpartydtcNo) {
    this.counterpartydtcNo = counterpartydtcNo;
    return this;
  }

   /**
   * Get counterpartydtcNo
   * @return counterpartydtcNo
  **/
  @ApiModelProperty(value = "")
  public String getCounterpartydtcNo() {
    return counterpartydtcNo;
  }

  public void setCounterpartydtcNo(String counterpartydtcNo) {
    this.counterpartydtcNo = counterpartydtcNo;
  }

  public IndexDTO eqlBenchmark(String eqlBenchmark) {
    this.eqlBenchmark = eqlBenchmark;
    return this;
  }

   /**
   * Get eqlBenchmark
   * @return eqlBenchmark
  **/
  @ApiModelProperty(value = "")
  public String getEqlBenchmark() {
    return eqlBenchmark;
  }

  public void setEqlBenchmark(String eqlBenchmark) {
    this.eqlBenchmark = eqlBenchmark;
  }

  public IndexDTO extIndexRef(String extIndexRef) {
    this.extIndexRef = extIndexRef;
    return this;
  }

   /**
   * Get extIndexRef
   * @return extIndexRef
  **/
  @ApiModelProperty(value = "")
  public String getExtIndexRef() {
    return extIndexRef;
  }

  public void setExtIndexRef(String extIndexRef) {
    this.extIndexRef = extIndexRef;
  }

  public IndexDTO indexCategory(String indexCategory) {
    this.indexCategory = indexCategory;
    return this;
  }

   /**
   * Get indexCategory
   * @return indexCategory
  **/
  @ApiModelProperty(value = "")
  public String getIndexCategory() {
    return indexCategory;
  }

  public void setIndexCategory(String indexCategory) {
    this.indexCategory = indexCategory;
  }

  public IndexDTO indexId(Long indexId) {
    this.indexId = indexId;
    return this;
  }

   /**
   * Get indexId
   * @return indexId
  **/
  @ApiModelProperty(value = "")
  public Long getIndexId() {
    return indexId;
  }

  public void setIndexId(Long indexId) {
    this.indexId = indexId;
  }

  public IndexDTO indexName(String indexName) {
    this.indexName = indexName;
    return this;
  }

   /**
   * Get indexName
   * @return indexName
  **/
  @ApiModelProperty(value = "")
  public String getIndexName() {
    return indexName;
  }

  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  public IndexDTO isActive(Boolean isActive) {
    this.isActive = isActive;
    return this;
  }

   /**
   * Get isActive
   * @return isActive
  **/
  @ApiModelProperty(value = "")
  public Boolean isIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public IndexDTO lookBack(Long lookBack) {
    this.lookBack = lookBack;
    return this;
  }

   /**
   * Get lookBack
   * @return lookBack
  **/
  @ApiModelProperty(value = "")
  public Long getLookBack() {
    return lookBack;
  }

  public void setLookBack(Long lookBack) {
    this.lookBack = lookBack;
  }

  public IndexDTO parentIndexId(Long parentIndexId) {
    this.parentIndexId = parentIndexId;
    return this;
  }

   /**
   * Get parentIndexId
   * @return parentIndexId
  **/
  @ApiModelProperty(value = "")
  public Long getParentIndexId() {
    return parentIndexId;
  }

  public void setParentIndexId(Long parentIndexId) {
    this.parentIndexId = parentIndexId;
  }

  public IndexDTO parentIndexName(String parentIndexName) {
    this.parentIndexName = parentIndexName;
    return this;
  }

   /**
   * Get parentIndexName
   * @return parentIndexName
  **/
  @ApiModelProperty(value = "")
  public String getParentIndexName() {
    return parentIndexName;
  }

  public void setParentIndexName(String parentIndexName) {
    this.parentIndexName = parentIndexName;
  }

  public IndexDTO securityDetailDTO(SecurityDetailDTO securityDetailDTO) {
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

  public IndexDTO securityId(Long securityId) {
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

  public IndexDTO sftrPmtFreqMulti(Integer sftrPmtFreqMulti) {
    this.sftrPmtFreqMulti = sftrPmtFreqMulti;
    return this;
  }

   /**
   * Get sftrPmtFreqMulti
   * @return sftrPmtFreqMulti
  **/
  @ApiModelProperty(value = "")
  public Integer getSftrPmtFreqMulti() {
    return sftrPmtFreqMulti;
  }

  public void setSftrPmtFreqMulti(Integer sftrPmtFreqMulti) {
    this.sftrPmtFreqMulti = sftrPmtFreqMulti;
  }

  public IndexDTO sftrPmtFreqTime(String sftrPmtFreqTime) {
    this.sftrPmtFreqTime = sftrPmtFreqTime;
    return this;
  }

   /**
   * Get sftrPmtFreqTime
   * @return sftrPmtFreqTime
  **/
  @ApiModelProperty(value = "")
  public String getSftrPmtFreqTime() {
    return sftrPmtFreqTime;
  }

  public void setSftrPmtFreqTime(String sftrPmtFreqTime) {
    this.sftrPmtFreqTime = sftrPmtFreqTime;
  }

  public IndexDTO sftrRateName(String sftrRateName) {
    this.sftrRateName = sftrRateName;
    return this;
  }

   /**
   * Get sftrRateName
   * @return sftrRateName
  **/
  @ApiModelProperty(value = "")
  public String getSftrRateName() {
    return sftrRateName;
  }

  public void setSftrRateName(String sftrRateName) {
    this.sftrRateName = sftrRateName;
  }

  public IndexDTO sftrRefPerMulti(Integer sftrRefPerMulti) {
    this.sftrRefPerMulti = sftrRefPerMulti;
    return this;
  }

   /**
   * Get sftrRefPerMulti
   * @return sftrRefPerMulti
  **/
  @ApiModelProperty(value = "")
  public Integer getSftrRefPerMulti() {
    return sftrRefPerMulti;
  }

  public void setSftrRefPerMulti(Integer sftrRefPerMulti) {
    this.sftrRefPerMulti = sftrRefPerMulti;
  }

  public IndexDTO sftrRefPerTime(String sftrRefPerTime) {
    this.sftrRefPerTime = sftrRefPerTime;
    return this;
  }

   /**
   * Get sftrRefPerTime
   * @return sftrRefPerTime
  **/
  @ApiModelProperty(value = "")
  public String getSftrRefPerTime() {
    return sftrRefPerTime;
  }

  public void setSftrRefPerTime(String sftrRefPerTime) {
    this.sftrRefPerTime = sftrRefPerTime;
  }

  public IndexDTO sftrResetFreqMulti(Integer sftrResetFreqMulti) {
    this.sftrResetFreqMulti = sftrResetFreqMulti;
    return this;
  }

   /**
   * Get sftrResetFreqMulti
   * @return sftrResetFreqMulti
  **/
  @ApiModelProperty(value = "")
  public Integer getSftrResetFreqMulti() {
    return sftrResetFreqMulti;
  }

  public void setSftrResetFreqMulti(Integer sftrResetFreqMulti) {
    this.sftrResetFreqMulti = sftrResetFreqMulti;
  }

  public IndexDTO sftrResetFreqTime(String sftrResetFreqTime) {
    this.sftrResetFreqTime = sftrResetFreqTime;
    return this;
  }

   /**
   * Get sftrResetFreqTime
   * @return sftrResetFreqTime
  **/
  @ApiModelProperty(value = "")
  public String getSftrResetFreqTime() {
    return sftrResetFreqTime;
  }

  public void setSftrResetFreqTime(String sftrResetFreqTime) {
    this.sftrResetFreqTime = sftrResetFreqTime;
  }

  public IndexDTO spread(Double spread) {
    this.spread = spread;
    return this;
  }

   /**
   * Get spread
   * @return spread
  **/
  @ApiModelProperty(value = "")
  public Double getSpread() {
    return spread;
  }

  public void setSpread(Double spread) {
    this.spread = spread;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IndexDTO indexDTO = (IndexDTO) o;
    return Objects.equals(this.bmAccountId, indexDTO.bmAccountId) &&
        Objects.equals(this.calendarDTO, indexDTO.calendarDTO) &&
        Objects.equals(this.calendarId, indexDTO.calendarId) &&
        Objects.equals(this.counterPartyDTO, indexDTO.counterPartyDTO) &&
        Objects.equals(this.counterpartyGroupAccountNo, indexDTO.counterpartyGroupAccountNo) &&
        Objects.equals(this.counterpartyGroupName, indexDTO.counterpartyGroupName) &&
        Objects.equals(this.counterpartyGroupdtcNo, indexDTO.counterpartyGroupdtcNo) &&
        Objects.equals(this.counterpartyId, indexDTO.counterpartyId) &&
        Objects.equals(this.counterpartydtcNo, indexDTO.counterpartydtcNo) &&
        Objects.equals(this.eqlBenchmark, indexDTO.eqlBenchmark) &&
        Objects.equals(this.extIndexRef, indexDTO.extIndexRef) &&
        Objects.equals(this.indexCategory, indexDTO.indexCategory) &&
        Objects.equals(this.indexId, indexDTO.indexId) &&
        Objects.equals(this.indexName, indexDTO.indexName) &&
        Objects.equals(this.isActive, indexDTO.isActive) &&
        Objects.equals(this.lookBack, indexDTO.lookBack) &&
        Objects.equals(this.parentIndexId, indexDTO.parentIndexId) &&
        Objects.equals(this.parentIndexName, indexDTO.parentIndexName) &&
        Objects.equals(this.securityDetailDTO, indexDTO.securityDetailDTO) &&
        Objects.equals(this.securityId, indexDTO.securityId) &&
        Objects.equals(this.sftrPmtFreqMulti, indexDTO.sftrPmtFreqMulti) &&
        Objects.equals(this.sftrPmtFreqTime, indexDTO.sftrPmtFreqTime) &&
        Objects.equals(this.sftrRateName, indexDTO.sftrRateName) &&
        Objects.equals(this.sftrRefPerMulti, indexDTO.sftrRefPerMulti) &&
        Objects.equals(this.sftrRefPerTime, indexDTO.sftrRefPerTime) &&
        Objects.equals(this.sftrResetFreqMulti, indexDTO.sftrResetFreqMulti) &&
        Objects.equals(this.sftrResetFreqTime, indexDTO.sftrResetFreqTime) &&
        Objects.equals(this.spread, indexDTO.spread);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bmAccountId, calendarDTO, calendarId, counterPartyDTO, counterpartyGroupAccountNo, counterpartyGroupName, counterpartyGroupdtcNo, counterpartyId, counterpartydtcNo, eqlBenchmark, extIndexRef, indexCategory, indexId, indexName, isActive, lookBack, parentIndexId, parentIndexName, securityDetailDTO, securityId, sftrPmtFreqMulti, sftrPmtFreqTime, sftrRateName, sftrRefPerMulti, sftrRefPerTime, sftrResetFreqMulti, sftrResetFreqTime, spread);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IndexDTO {\n");
    
    sb.append("    bmAccountId: ").append(toIndentedString(bmAccountId)).append("\n");
    sb.append("    calendarDTO: ").append(toIndentedString(calendarDTO)).append("\n");
    sb.append("    calendarId: ").append(toIndentedString(calendarId)).append("\n");
    sb.append("    counterPartyDTO: ").append(toIndentedString(counterPartyDTO)).append("\n");
    sb.append("    counterpartyGroupAccountNo: ").append(toIndentedString(counterpartyGroupAccountNo)).append("\n");
    sb.append("    counterpartyGroupName: ").append(toIndentedString(counterpartyGroupName)).append("\n");
    sb.append("    counterpartyGroupdtcNo: ").append(toIndentedString(counterpartyGroupdtcNo)).append("\n");
    sb.append("    counterpartyId: ").append(toIndentedString(counterpartyId)).append("\n");
    sb.append("    counterpartydtcNo: ").append(toIndentedString(counterpartydtcNo)).append("\n");
    sb.append("    eqlBenchmark: ").append(toIndentedString(eqlBenchmark)).append("\n");
    sb.append("    extIndexRef: ").append(toIndentedString(extIndexRef)).append("\n");
    sb.append("    indexCategory: ").append(toIndentedString(indexCategory)).append("\n");
    sb.append("    indexId: ").append(toIndentedString(indexId)).append("\n");
    sb.append("    indexName: ").append(toIndentedString(indexName)).append("\n");
    sb.append("    isActive: ").append(toIndentedString(isActive)).append("\n");
    sb.append("    lookBack: ").append(toIndentedString(lookBack)).append("\n");
    sb.append("    parentIndexId: ").append(toIndentedString(parentIndexId)).append("\n");
    sb.append("    parentIndexName: ").append(toIndentedString(parentIndexName)).append("\n");
    sb.append("    securityDetailDTO: ").append(toIndentedString(securityDetailDTO)).append("\n");
    sb.append("    securityId: ").append(toIndentedString(securityId)).append("\n");
    sb.append("    sftrPmtFreqMulti: ").append(toIndentedString(sftrPmtFreqMulti)).append("\n");
    sb.append("    sftrPmtFreqTime: ").append(toIndentedString(sftrPmtFreqTime)).append("\n");
    sb.append("    sftrRateName: ").append(toIndentedString(sftrRateName)).append("\n");
    sb.append("    sftrRefPerMulti: ").append(toIndentedString(sftrRefPerMulti)).append("\n");
    sb.append("    sftrRefPerTime: ").append(toIndentedString(sftrRefPerTime)).append("\n");
    sb.append("    sftrResetFreqMulti: ").append(toIndentedString(sftrResetFreqMulti)).append("\n");
    sb.append("    sftrResetFreqTime: ").append(toIndentedString(sftrResetFreqTime)).append("\n");
    sb.append("    spread: ").append(toIndentedString(spread)).append("\n");
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

