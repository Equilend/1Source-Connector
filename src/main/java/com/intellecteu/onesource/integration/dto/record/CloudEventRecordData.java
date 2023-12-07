package com.intellecteu.onesource.integration.dto.record;

import java.util.List;

/**
 * The implementation should provide the optional data format according to the CloudEvents <a
 * href="https://github.com/cloudevents/spec">specification</a>
 */
public interface CloudEventRecordData extends RecordData {

    List<RelatedObject> getRelatedObjects();

}
