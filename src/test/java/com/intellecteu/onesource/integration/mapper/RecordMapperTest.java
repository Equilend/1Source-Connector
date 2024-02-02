package com.intellecteu.onesource.integration.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.dto.record.CloudEvent;
import com.intellecteu.onesource.integration.dto.record.CloudEventData;
import com.intellecteu.onesource.integration.dto.record.RelatedObject;
import com.intellecteu.onesource.integration.exception.ConvertException;
import com.intellecteu.onesource.integration.repository.entity.onesource.CloudEventEntity;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecordMapperTest {

    private RecordMapper recordMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        recordMapper = new RecordMapper(objectMapper);
    }

    @Test
    void testConvertObject_shouldConvertCloudEventToJsonAndReadBack() throws Exception {
        var relatedObject = new RelatedObject("id", "type");
        var eventData = new CloudEventData();
        eventData.setMessage("testMessage");
        eventData.setRelatedObjects(List.of(relatedObject));

        String expectedMessage = "{\"message\":\"testMessage\",\"relatedObjects\":[{\"relatedObjectId\":\"id\",\"relatedObjectType\":\"type\"}]}";

        String actualMessage = objectMapper.writeValueAsString(eventData);
        CloudEventData actualObject = objectMapper.readValue(actualMessage, CloudEventData.class);

        assertEquals(expectedMessage, actualMessage);
        assertEquals(eventData, actualObject);
    }

    @Test
    void testConvertObject_shouldConvertCloudEventToJsonAndReadBack_whenMultipleRelatedObjects() throws Exception {
        var relatedObject1 = new RelatedObject("id1", "type1");
        var relatedObject2 = new RelatedObject("id2", "type2");
        var eventData = new CloudEventData();
        eventData.setMessage("testMessage");
        eventData.setRelatedObjects(List.of(relatedObject1, relatedObject2));

        String expectedMessage = """
            {"message":"testMessage","relatedObjects":[{"relatedObjectId":"id1","relatedObjectType":"type1"},{"relatedObjectId":"id2","relatedObjectType":"type2"}]}""";

        String actualMessage = objectMapper.writeValueAsString(eventData);
        CloudEventData actualObject = objectMapper.readValue(actualMessage, CloudEventData.class);

        assertEquals(expectedMessage, actualMessage);
        assertEquals(eventData, actualObject);
    }

    @Test
    void testCloudEventConvert_shouldConvertToCloudEvent_whenAllDataAvailable() {
        CloudEventEntity entity = ModelTestFactory.buildCloudEventEntity();
        final CloudEvent cloudEvent = recordMapper.toCloudEvent(entity);

        assertEquals(entity.getId(), cloudEvent.getId());
        assertEquals(entity.getSpecVersion(), cloudEvent.getSpecVersion());
        assertEquals(entity.getType(), cloudEvent.getType());
        assertEquals(entity.getSource(), cloudEvent.getSource());
        assertEquals(entity.getSubject(), cloudEvent.getSubject());
        assertEquals(entity.getTime(), cloudEvent.getTime());
        assertEquals(entity.getRelatedProcess(), cloudEvent.getRelatedProcess());
        assertEquals(entity.getRelatedSubProcess(), cloudEvent.getRelatedSubProcess());
        assertEquals(entity.getDataContentType(), cloudEvent.getDataContentType());
    }

    @Test
    void testCloudEventConvert_shouldThrowConvertException_whenConvertIssue() {
        ConvertException exception = assertThrows(
            ConvertException.class,
            () -> recordMapper.toCloudEvent(null)
        );
        assertNotNull(exception);
    }
}
