package com.intellecteu.onesource.integration.dto.record;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CloudEvent implements Record {

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
    private String eventData;

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Recordable> T getData() {
        var om = new ObjectMapper();
        try {
            final CloudEventData cloudEventData = om.readValue(eventData, CloudEventData.class);
            return (T) cloudEventData;
        } catch (JsonProcessingException e) {
            log.warn("Parse exception during convert data: " + e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
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
}
