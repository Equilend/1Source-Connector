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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * NQueryRequest
 */

public class NQueryRequest {
  @JsonProperty("aggregators")
  private List<SAggregator> aggregators = null;

  @JsonProperty("count")
  private Long count = null;

  @JsonProperty("exclude")
  private List<String> exclude = null;

  @JsonProperty("groupBy")
  private List<String> groupBy = null;

  @JsonProperty("include")
  private List<String> include = null;

  @JsonProperty("nQuery")
  private NQuery nQuery = null;

  @JsonProperty("orderBy")
  private List<SOrderBy> orderBy = null;

  @JsonProperty("sqlGroupBy")
  private List<String> sqlGroupBy = null;

  @JsonProperty("start")
  private Long start = null;

  public NQueryRequest aggregators(List<SAggregator> aggregators) {
    this.aggregators = aggregators;
    return this;
  }

  public NQueryRequest addAggregatorsItem(SAggregator aggregatorsItem) {
    if (this.aggregators == null) {
      this.aggregators = new ArrayList<>();
    }
    this.aggregators.add(aggregatorsItem);
    return this;
  }

   /**
   * Get aggregators
   * @return aggregators
  **/
  @ApiModelProperty(value = "")
  public List<SAggregator> getAggregators() {
    return aggregators;
  }

  public void setAggregators(List<SAggregator> aggregators) {
    this.aggregators = aggregators;
  }

  public NQueryRequest count(Long count) {
    this.count = count;
    return this;
  }

   /**
   * Get count
   * @return count
  **/
  @ApiModelProperty(value = "")
  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

  public NQueryRequest exclude(List<String> exclude) {
    this.exclude = exclude;
    return this;
  }

  public NQueryRequest addExcludeItem(String excludeItem) {
    if (this.exclude == null) {
      this.exclude = new ArrayList<>();
    }
    this.exclude.add(excludeItem);
    return this;
  }

   /**
   * Get exclude
   * @return exclude
  **/
  @ApiModelProperty(value = "")
  public List<String> getExclude() {
    return exclude;
  }

  public void setExclude(List<String> exclude) {
    this.exclude = exclude;
  }

  public NQueryRequest groupBy(List<String> groupBy) {
    this.groupBy = groupBy;
    return this;
  }

  public NQueryRequest addGroupByItem(String groupByItem) {
    if (this.groupBy == null) {
      this.groupBy = new ArrayList<>();
    }
    this.groupBy.add(groupByItem);
    return this;
  }

   /**
   * Get groupBy
   * @return groupBy
  **/
  @ApiModelProperty(value = "")
  public List<String> getGroupBy() {
    return groupBy;
  }

  public void setGroupBy(List<String> groupBy) {
    this.groupBy = groupBy;
  }

  public NQueryRequest include(List<String> include) {
    this.include = include;
    return this;
  }

  public NQueryRequest addIncludeItem(String includeItem) {
    if (this.include == null) {
      this.include = new ArrayList<>();
    }
    this.include.add(includeItem);
    return this;
  }

   /**
   * Get include
   * @return include
  **/
  @ApiModelProperty(value = "")
  public List<String> getInclude() {
    return include;
  }

  public void setInclude(List<String> include) {
    this.include = include;
  }

  public NQueryRequest nQuery(NQuery nQuery) {
    this.nQuery = nQuery;
    return this;
  }

   /**
   * Get nQuery
   * @return nQuery
  **/
  @ApiModelProperty(value = "")
  public NQuery getNQuery() {
    return nQuery;
  }

  public void setNQuery(NQuery nQuery) {
    this.nQuery = nQuery;
  }

  public NQueryRequest orderBy(List<SOrderBy> orderBy) {
    this.orderBy = orderBy;
    return this;
  }

  public NQueryRequest addOrderByItem(SOrderBy orderByItem) {
    if (this.orderBy == null) {
      this.orderBy = new ArrayList<>();
    }
    this.orderBy.add(orderByItem);
    return this;
  }

   /**
   * Get orderBy
   * @return orderBy
  **/
  @ApiModelProperty(value = "")
  public List<SOrderBy> getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(List<SOrderBy> orderBy) {
    this.orderBy = orderBy;
  }

  public NQueryRequest sqlGroupBy(List<String> sqlGroupBy) {
    this.sqlGroupBy = sqlGroupBy;
    return this;
  }

  public NQueryRequest addSqlGroupByItem(String sqlGroupByItem) {
    if (this.sqlGroupBy == null) {
      this.sqlGroupBy = new ArrayList<>();
    }
    this.sqlGroupBy.add(sqlGroupByItem);
    return this;
  }

   /**
   * Get sqlGroupBy
   * @return sqlGroupBy
  **/
  @ApiModelProperty(value = "")
  public List<String> getSqlGroupBy() {
    return sqlGroupBy;
  }

  public void setSqlGroupBy(List<String> sqlGroupBy) {
    this.sqlGroupBy = sqlGroupBy;
  }

  public NQueryRequest start(Long start) {
    this.start = start;
    return this;
  }

   /**
   * Get start
   * @return start
  **/
  @ApiModelProperty(value = "")
  public Long getStart() {
    return start;
  }

  public void setStart(Long start) {
    this.start = start;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NQueryRequest nqueryRequest = (NQueryRequest) o;
    return Objects.equals(this.aggregators, nqueryRequest.aggregators) &&
        Objects.equals(this.count, nqueryRequest.count) &&
        Objects.equals(this.exclude, nqueryRequest.exclude) &&
        Objects.equals(this.groupBy, nqueryRequest.groupBy) &&
        Objects.equals(this.include, nqueryRequest.include) &&
        Objects.equals(this.nQuery, nqueryRequest.nQuery) &&
        Objects.equals(this.orderBy, nqueryRequest.orderBy) &&
        Objects.equals(this.sqlGroupBy, nqueryRequest.sqlGroupBy) &&
        Objects.equals(this.start, nqueryRequest.start);
  }

  @Override
  public int hashCode() {
    return Objects.hash(aggregators, count, exclude, groupBy, include, nQuery, orderBy, sqlGroupBy, start);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NQueryRequest {\n");
    
    sb.append("    aggregators: ").append(toIndentedString(aggregators)).append("\n");
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    exclude: ").append(toIndentedString(exclude)).append("\n");
    sb.append("    groupBy: ").append(toIndentedString(groupBy)).append("\n");
    sb.append("    include: ").append(toIndentedString(include)).append("\n");
    sb.append("    nQuery: ").append(toIndentedString(nQuery)).append("\n");
    sb.append("    orderBy: ").append(toIndentedString(orderBy)).append("\n");
    sb.append("    sqlGroupBy: ").append(toIndentedString(sqlGroupBy)).append("\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
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

