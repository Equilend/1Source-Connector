package com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent;

import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.SystemEventData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class IntegrationCloudEvent implements CloudEventRecord {

    private CloudEventMetadata metadata;
    private SystemEventData data;

}
