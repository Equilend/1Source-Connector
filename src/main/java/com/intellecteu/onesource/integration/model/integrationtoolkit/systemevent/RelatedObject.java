package com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.NOT_APPLICABLE;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
@Embeddable
public class RelatedObject {

    private String relatedObjectId;
    private String relatedObjectType;

    public static RelatedObject notApplicable() {
        return new RelatedObject(NOT_APPLICABLE, NOT_APPLICABLE);
    }

}
