package com.intellecteu.onesource.integration.services.systemevent;

import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.record.CloudEventRecord;
import com.intellecteu.onesource.integration.model.enums.IntegrationProcess;

/**
 * Create an instance of CloudEventRecord according to the CloudEventBuildRequest and Integration process.
 *
 * @param <T> CloudEventRecord
 */
public interface CloudEventBuilder<T extends CloudEventRecord> {

    /**
     * Return the version of integration process
     *
     * @return IntegrationProcess
     */
    IntegrationProcess getVersion();

    /**
     * Create an instance of CloudEventRecord
     *
     * @param buildRequest CloudEventBuildRequest
     * @return CloudEventRecord instance
     */
    T build(CloudEventBuildRequest buildRequest);

}
