package com.intellecteu.onesource.integration.dto.record;

import java.net.URI;

/**
 * The implementation should provide the required attributes format according
 * to the CloudEvents <a href="https://github.com/cloudevents/spec">specification</a>
 */
public interface CloudEventRecordMetadata extends RecordMetadata {
  String getSpecVersion();
  String getType();
  URI getSource();
  String getId();

}
