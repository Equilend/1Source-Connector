package com.intellecteu.onesource.integration.exception;

import com.intellecteu.onesource.integration.dto.ExceptionMessageDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationException extends Exception {

  private ExceptionMessageDto dto;

  public ValidationException() {
    super();
  }

  public ValidationException(String message) {
    super(message);
  }

  public ValidationException(ExceptionMessageDto dto) {
    super(dto.getExceptionMessage());
    this.dto = dto;
  }

}
