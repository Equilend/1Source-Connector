package com.intellecteu.onesource.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellecteu.onesource.integration.enums.FlowStatus;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.AgreementStatus;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.services.Reconcilable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static com.intellecteu.onesource.integration.constant.AgreementConstant.Field.TRADE;
import static com.intellecteu.onesource.integration.utils.ExceptionUtils.throwFieldMissedException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Builder
public class AgreementDto implements Reconcilable {

  private long id;
  private String agreementId;
  @JsonProperty("status")
  private AgreementStatus status;
  @JsonProperty("lastUpdateDatetime")
  private LocalDateTime lastUpdateDatetime;
  @JsonProperty("trade")
  private TradeAgreementDto trade;
  private EventType eventType;
  private FlowStatus flowStatus;

  @Override
  public void validateForReconciliation() throws ValidationException {
    if (trade == null) {
      throwFieldMissedException(TRADE);
    } else {
      trade.validateForReconciliation();
    }
  }
}
