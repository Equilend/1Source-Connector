package com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent;

import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.FieldSource;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FieldImpacted {

    @Enumerated(EnumType.STRING)
    private FieldSource fieldSource;

    private String fieldName;

    private String fieldValue;

    @Enumerated(EnumType.STRING)
    private FieldExceptionType fieldExceptionType;

}
