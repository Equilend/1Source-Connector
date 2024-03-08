package com.intellecteu.onesource.integration.model;

import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.FieldSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessExceptionDetails {
    private FieldSource source;
    private String fieldName;
    private String fieldValue;
    private FieldExceptionType fieldExceptionType;

}
