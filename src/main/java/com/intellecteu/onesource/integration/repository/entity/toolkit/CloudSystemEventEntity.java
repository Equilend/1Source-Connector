package com.intellecteu.onesource.integration.repository.entity.toolkit;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventProcessingStatus;
import com.intellecteu.onesource.integration.repository.entity.onesource.SystemEventDataEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Builder
@ToString
@JsonIgnoreProperties(value = {"processingStatus"})
@Table(name = "event_record")
public class CloudSystemEventEntity {

    @Id
    @Column(name = "id")
    private String id;

    @JsonProperty("specversion")
    @Column(name = "specversion", nullable = false)
    private String specVersion;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "source", nullable = false)
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

    @OneToOne(mappedBy = "cloudEvent", cascade = CascadeType.ALL)
    private SystemEventDataEntity data;

    @Column(name = "processingstatus")
    @Enumerated(value = EnumType.STRING)
    private CloudEventProcessingStatus processingStatus;

    public void setData(SystemEventDataEntity data) {
        this.data = data;
        this.data.setCloudEvent(this);
    }

    @PrePersist
    @PreUpdate
    void setIdIfMissed() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CloudSystemEventEntity that = (CloudSystemEventEntity) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass());
    }

    public CloudSystemEventEntity() {
        id = UUID.randomUUID().toString();
    }

    public CloudSystemEventEntity(String id, String specVersion, String type, String source, String subject,
        LocalDateTime time, String relatedProcess, String relatedSubProcess, String dataContentType,
        SystemEventDataEntity data, CloudEventProcessingStatus processingStatus) {
        this.id = id;
        this.specVersion = specVersion;
        this.type = type;
        this.source = source;
        this.subject = subject;
        this.time = time;
        this.relatedProcess = relatedProcess;
        this.relatedSubProcess = relatedSubProcess;
        this.dataContentType = dataContentType;
        this.data = data;
        this.processingStatus = processingStatus;
    }
}
