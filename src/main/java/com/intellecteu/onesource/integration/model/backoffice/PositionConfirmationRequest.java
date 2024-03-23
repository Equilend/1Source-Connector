package com.intellecteu.onesource.integration.model.backoffice;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionConfirmationRequest {

    private Integer userId;
    private String userName;
    private Long positionId;
    private Long tradeId;
    private String ledgerId;
    private PositionInstruction instructions;

}
