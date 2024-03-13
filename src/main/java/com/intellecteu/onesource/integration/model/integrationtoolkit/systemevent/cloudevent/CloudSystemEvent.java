package com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.Record;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.Recordable;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.SystemEventData;
import jakarta.persistence.Column;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
public class CloudSystemEvent implements Record {

    private String id;
    @JsonProperty("specversion")
    private String specVersion;
    private String type;
    private String source;
    private String subject;
    @JsonAlias({"Time", "time"})
    private LocalDateTime time;
    @JsonProperty("relatedprocess")
    private String relatedProcess;
    @JsonProperty("relatedsubprocess")
    private String relatedSubProcess;
    @JsonProperty("datacontenttype")
    private String dataContentType;
    @Column(name = "data")
    private SystemEventData eventData;

    @SuppressWarnings("unchecked")
    @Override
    @JsonIgnore
    public <T extends Recordable> T getData() {
        return (T) eventData;
    }

    @SuppressWarnings("unchecked")
    @Override
    @JsonIgnore
    public <T extends Recordable> T getMetadata() {
        return (T) CloudEventMetadata.builder()
            .id(id)
            .specVersion(specVersion)
            .type(type)
            .source(URI.create(source))
            .subject(subject)
            .time(time)
            .relatedProcess(relatedProcess)
            .relatedSubProcess(relatedSubProcess)
            .dataContentType(dataContentType)
            .build();
    }

    public CloudSystemEvent() {
        id = UUID.randomUUID().toString();
    }

    public CloudSystemEvent(String id, String specVersion, String type, String source, String subject,
        LocalDateTime time,
        String relatedProcess, String relatedSubProcess, String dataContentType, SystemEventData eventData) {
        this.id = id;
        this.specVersion = specVersion;
        this.type = type;
        this.source = source;
        this.subject = subject;
        this.time = time;
        this.relatedProcess = relatedProcess;
        this.relatedSubProcess = relatedSubProcess;
        this.dataContentType = dataContentType;
        this.eventData = eventData;
    }
}
