package com.intellecteu.onesource.integration.dto.record;

/**
 * The implementation of record should provide metadata and data to be recorded
 */
public interface Record {

    <T extends Recordable> T getMetadata();

    <R extends Recordable> R getData();

}
