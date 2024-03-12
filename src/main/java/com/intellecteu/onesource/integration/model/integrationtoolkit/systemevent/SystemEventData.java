package com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventRecordData;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class SystemEventData implements CloudEventRecordData, Recordable {

    @JsonIgnore
    private String eventDataId;
    private String message;
    private List<FieldImpacted> fieldsImpacted;
    private List<RelatedObject> relatedObjects;

}
