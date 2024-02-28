package com.intellecteu.onesource.integration.services.systemevent;

import com.intellecteu.onesource.integration.dto.record.RecordRequest;
import com.intellecteu.onesource.integration.dto.record.Recordable;

/**
 * The service to record exceptions or events raised by the Integration toolkit
 */
public interface RecordService<T extends RecordRequest<? extends Recordable, ? extends Recordable>> {

    void record(T recordRequest);

}
