package com.intellecteu.onesource.integration.dto.record;

import java.io.Serializable;

/**
 * The implementation of record should provide metadata and data to be recorded
 */
public interface Record extends Serializable {

    <T extends Recordable> T getMetadata();

    <R extends Recordable> R getData();

}
