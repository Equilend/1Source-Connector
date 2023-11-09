package com.intellecteu.onesource.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionMessageDto {

  private String value;
  private String exceptionMessage;

}
