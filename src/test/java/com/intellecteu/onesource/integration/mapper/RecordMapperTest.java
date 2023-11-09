package com.intellecteu.onesource.integration.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.dto.record.CloudEventData;
import com.intellecteu.onesource.integration.dto.record.RelatedObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecordMapperTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
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
}