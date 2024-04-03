package com.intellecteu.onesource.integration.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.Record;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JsonMapper {

    private final ObjectMapper objectMapper;

    @Named("recordToJson")
    public <T extends Record> String recordToJson(T recordObject) {
        try {
            return objectMapper.writeValueAsString(recordObject);
        } catch (IOException e) {
            log.debug("Error on parsing: {}", e.getMessage());
            return null;
        }
    }

}
