package com.intellecteu.onesource.integration.services.systemevent;

import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.record.CloudEventRecord;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;

/**
 * Factory to build CloudEventRecord instances
 *
 * @param <T> CloudEventRecord
 */
public interface CloudEventFactory<T extends CloudEventRecord> {

    /**
     * Create a CloudEventRecord
     *
     * @param buildRequest CloudEventBuildRequest
     * @return CloudEventRecord instance
     */
    T createRecord(CloudEventBuildRequest buildRequest);

    /**
     * Get cloud event builder by IntegrationProcess type
     *
     * @param type IntegrationProcess
     * @return IntegrationCloudEventBuilder instance
     */
    IntegrationCloudEventBuilder eventBuilder(IntegrationProcess type);

}
