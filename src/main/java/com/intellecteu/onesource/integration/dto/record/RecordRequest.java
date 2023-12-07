package com.intellecteu.onesource.integration.dto.record;

/**
 * A record request is an object that gathers the data with related metadata that should be transformed into the
 * recordable object
 *
 * @param <T> metadata
 * @param <R> data
 */
public interface RecordRequest<T extends Recordable, R extends Recordable> {

    T getMetadata();

    R getData();

}
