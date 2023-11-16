package com.intellecteu.onesource.integration.model;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(value = {"processingStatus"} )
@Table(name = "event_record")
public class CloudEventEntity {

  @Id
  @Column(name = "id")
  private String id;

  @JsonProperty("specversion")
  @Column(name = "specversion")
  private String specVersion;

  @Column(name = "type")
  private String type;

  @Column(name = "source")
  private String source;

  @Column(name = "subject")
  private String subject;

  @JsonAlias({"Time", "time"})
  @Column(name = "time")
  private LocalDateTime time;

  @JsonProperty("relatedprocess")
  @Column(name = "relatedprocess")
  private String relatedProcess;

  @JsonProperty("relatedsubprocess")
  @Column(name = "relatedsubprocess")
  private String relatedSubProcess;

  @JsonProperty("datacontenttype")
  @Column(name = "datacontenttype")
  private String dataContentType;

  @Column(name = "data")
  private String data;

  @Column(name = "processingstatus")
  @Enumerated(value = EnumType.STRING)
  private ProcessingStatus processingStatus;

  @PrePersist
  @PreUpdate
  void setIdIfMissed() {
    if (id == null) {
      id = UUID.randomUUID().toString();
    }
  }

}
