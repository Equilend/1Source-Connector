package com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent;

import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RecordRequest;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.SystemEventData;
import com.intellecteu.onesource.integration.model.onesource.EventType;
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
public class CloudEventBuildRequest implements RecordRequest<CloudEventMetadata, SystemEventData> {

    RecordType recordType;
    String subject;
    IntegrationProcess relatedProcess;
    IntegrationSubProcess relatedSubProcess;
    HttpStatus statusCode;
    EventType eventType;
    SystemEventData data;

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
