package com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent;

import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RecordMetadata;
import java.net.URI;

/**
 * The implementation should provide the required attributes format according to the CloudEvents <a
 * href="https://github.com/cloudevents/spec">specification</a>
 */
public interface CloudEventRecordMetadata extends RecordMetadata {

    String getSpecVersion();

    String getType();

    URI getSource();

    String getId();

}
