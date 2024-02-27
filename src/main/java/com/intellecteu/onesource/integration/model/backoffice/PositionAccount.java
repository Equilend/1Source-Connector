package com.intellecteu.onesource.integration.model.backoffice;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionAccount {

    private Long id;
    private Long accountId;
    private String shortName;
    private String lei;
    private Long oneSourceId;
    private Long dtc;

}
