package com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent;

import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RecordData;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import java.util.List;

/**
 * The implementation should provide the optional data format according to the CloudEvents <a
 * href="https://github.com/cloudevents/spec">specification</a>
 */
public interface CloudEventRecordData extends RecordData {

    String getMessage();

    List<FieldImpacted> getFieldsImpacted();

    List<RelatedObject> getRelatedObjects();

}
