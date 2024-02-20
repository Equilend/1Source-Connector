package com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent;

import java.io.Serializable;

/**
 * The implementation of record should provide metadata and data to be recorded
 */
public interface Record extends Serializable {

    <T extends Recordable> T getMetadata();

    <R extends Recordable> R getData();

}
