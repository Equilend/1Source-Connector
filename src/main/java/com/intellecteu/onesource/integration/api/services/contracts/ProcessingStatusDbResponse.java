package com.intellecteu.onesource.integration.api.services.contracts;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;

/**
 * Projection to return only one field from a heavy entity
 */
public interface ProcessingStatusDbResponse {

    ProcessingStatus getProcessingStatus();

}
