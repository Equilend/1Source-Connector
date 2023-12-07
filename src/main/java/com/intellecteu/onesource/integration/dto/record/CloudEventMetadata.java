package com.intellecteu.onesource.integration.dto.record;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class CloudEventMetadata implements CloudEventRecordMetadata, Recordable {

    @Value("${cloudevents.specversion}")
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
}
