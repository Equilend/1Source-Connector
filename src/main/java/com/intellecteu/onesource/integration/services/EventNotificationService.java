package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.record.Record;

public interface EventNotificationService<T extends Record> {

  void sendAllEvents();

  void sendEvent(T event);

}
