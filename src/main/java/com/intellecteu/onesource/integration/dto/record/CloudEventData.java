package com.intellecteu.onesource.integration.dto.record;

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
public class CloudEventData implements CloudEventRecordData, Recordable {

    private String message;
    private List<RelatedObject> relatedObjects;

}
