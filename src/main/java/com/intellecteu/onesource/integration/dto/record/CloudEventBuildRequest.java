package com.intellecteu.onesource.integration.dto.record;

import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import com.intellecteu.onesource.integration.model.EventType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudEventBuildRequest implements RecordRequest<CloudEventMetadata, CloudEventData> {

    RecordType recordType;
    String subject;
    IntegrationProcess relatedProcess;
    IntegrationSubProcess relatedSubProcess;
    HttpStatus statusCode;
    EventType eventType;
    CloudEventData data;

    @Override
    public CloudEventMetadata getMetadata() {
        return CloudEventMetadata.builder()
            .type(recordType.name())
            .subject(subject)
            .relatedProcess(relatedProcess.name())
            .relatedSubProcess(relatedSubProcess.name())
            .build();
    }
}
