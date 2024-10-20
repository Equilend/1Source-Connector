package com.intellecteu.onesource.integration.services.systemevent;

import static java.lang.String.format;

import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.IntegrationCloudEvent;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CloudEventFactoryImpl implements CloudEventFactory<IntegrationCloudEvent> {

    private final Map<IntegrationProcess, IntegrationCloudEventBuilder> eventBuilderByProcess;

    @Override
    public IntegrationCloudEvent createRecord(CloudEventBuildRequest buildRequest) {
        var eventBuilder = Optional.ofNullable(eventBuilderByProcess.get(buildRequest.getRelatedProcess()))
            .orElseThrow(() -> new RuntimeException(format(
                "This integration process: %s is not yet implemented", buildRequest.getRelatedProcess())));
        return eventBuilder.build(buildRequest);
    }

    @Override
    public IntegrationCloudEventBuilder eventBuilder(IntegrationProcess type) {
        return Optional.ofNullable(eventBuilderByProcess.get(type))
            .orElseThrow(() -> new RuntimeException(format(
                "This integration process: %s is not yet implemented", type)));
    }
}
