package com.intellecteu.onesource.integration.services.systemevent;

import com.intellecteu.onesource.integration.model.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.model.enums.RecordType;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.IntegrationCloudEvent;
import java.util.Optional;
import org.springframework.lang.NonNull;

/**
 * Service to record events according to the CloudEvents <a
 * href="https://github.com/cloudevents/spec">specification</a>
 */
public interface CloudEventRecordService extends RecordService<CloudEventBuildRequest> {

    /**
     * Get the creation factory for IntegrationCloudEvent
     *
     * @return CloudEventFactory<IntegrationCloudEvent>
     */
    CloudEventFactory<IntegrationCloudEvent> getFactory();

    /**
     * Get the Event id for the Integration Toolkit Technical Issue Type by relatedObjectId and subProcess
     *
     * @param relatedObjectId String the object related to the cloud event record
     * @param subProcess IntegrationSubProcess the Integration Toolkit subprocess
     * @return Optional of String for the captured event id or Empty Optional if not found
     */
    Optional<String> getToolkitCloudEventId(String relatedObjectId, IntegrationSubProcess subProcess,
        RecordType recordType);

    // temporary until new changes will be deployed to capture related object id for RERATE_TRADE_CREATED type
    Optional<String> getToolkitCloudEventIdForRerateWorkaround(String relatedObjectId, IntegrationSubProcess subProcess,
        RecordType recordType);

    /**
     * Update cloud event time by the event id
     *
     * @param eventId String
     */
    void updateTime(@NonNull String eventId);

}
