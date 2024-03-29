package com.intellecteu.onesource.integration.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.SystemEventData;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudSystemEventDto {

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
    private SystemEventData data;

}
