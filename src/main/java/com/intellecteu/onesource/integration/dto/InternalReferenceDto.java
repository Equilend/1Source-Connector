package com.intellecteu.onesource.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternalReferenceDto {

    private String brokerCd;
    private Long accountId;
    private String internalRefId;
}
