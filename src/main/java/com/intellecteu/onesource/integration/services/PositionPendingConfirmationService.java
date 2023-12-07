package com.intellecteu.onesource.integration.services;

public interface PositionPendingConfirmationService {

    /**
     * The SPIRE positions recorded in the integration middleware with a processing status different from "CANCELED" and
     * "SETTLED" have been updated in SPIRE and need to be updated in the Integration toolkit.
     */
    void processUpdatedPositions();

}
