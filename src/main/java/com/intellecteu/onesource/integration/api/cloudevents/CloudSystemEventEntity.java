package com.intellecteu.onesource.integration.api.cloudevents;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.api.ProcessingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
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
@JsonIgnoreProperties(value = {"processingStatus"})
@Table(name = "event_record")
class CloudSystemEventEntity {

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
