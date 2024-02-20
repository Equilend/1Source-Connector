package com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.Recordable;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Data
@Builder
@Slf4j
public class CloudEventMetadata implements CloudEventRecordMetadata, Recordable {


    @JsonProperty("specversion")
    private String specVersion;
    private String type;
    private URI source;
    private String subject;
    private String id;
    @JsonAlias({"Time", "time"})
    private LocalDateTime time;
    @JsonProperty("relatedprocess")
    private String relatedProcess;
    @JsonProperty("relatedsubprocess")
    private String relatedSubProcess;
    @JsonProperty("datacontenttype")
    private String dataContentType;


    @Override
    public String getMetadata() {
        return this.toString();
    }

    public CloudEventMetadata() {
        id = UUID.randomUUID().toString();
    }

    public CloudEventMetadata(@Value("${cloudevents.specversion}") String specVersion,
        String type, URI source, String subject, String id,
        LocalDateTime time,
        String relatedProcess, String relatedSubProcess, String dataContentType) {
        this.specVersion = specVersion;
        this.type = type;
        this.source = source;
        this.subject = subject;
        this.id = id;
        this.time = time;
        this.relatedProcess = relatedProcess;
        this.relatedSubProcess = relatedSubProcess;
        this.dataContentType = dataContentType;
    }
}
