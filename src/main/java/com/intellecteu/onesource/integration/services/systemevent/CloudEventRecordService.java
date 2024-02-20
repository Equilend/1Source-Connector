package com.intellecteu.onesource.integration.services.systemevent;

import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.IntegrationCloudEvent;

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

}
