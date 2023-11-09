package com.intellecteu.onesource.integration.exception;

import com.intellecteu.onesource.integration.dto.ExceptionMessageDto;

public class RequiredDataMissedException extends ValidationException {

  public static final String REQUIRED_DATA_MISSED_MSG = "Required data: [%s] must not be null!";

  public RequiredDataMissedException() {
  }

  public RequiredDataMissedException(ExceptionMessageDto dto) {
    super(dto);
  }

}
