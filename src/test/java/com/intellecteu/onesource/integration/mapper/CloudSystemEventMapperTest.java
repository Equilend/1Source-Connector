package com.intellecteu.onesource.integration.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.intellecteu.onesource.integration.EntityTestFactory;
import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.config.ApplicationTestConfig;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.SystemEventData;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudEventMetadata;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.CloudSystemEvent;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.cloudevent.IntegrationCloudEvent;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import java.net.URI;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    ApplicationTestConfig.class})
public class CloudSystemEventMapperTest {

    @Autowired
    JsonMapper jsonMapper;

    private CloudSystemEventMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CloudSystemEventMapper.class);
        mapper.setJsonMapper(jsonMapper);
    }

    @Test
    void entityToModel() {
        CloudSystemEventEntity entity = EntityTestFactory.createSystemEventEntity();
        final CloudSystemEvent cloudEvent = mapper.toCloudEvent(entity);

        assertEquals(entity.getSpecVersion(), cloudEvent.getSpecVersion());
        assertEquals(entity.getType(), cloudEvent.getType());
        assertEquals(entity.getSource(), cloudEvent.getSource());
        assertEquals(entity.getSubject(), cloudEvent.getSubject());
        assertEquals(entity.getTime(), cloudEvent.getTime());
        assertEquals(entity.getRelatedProcess(), cloudEvent.getRelatedProcess());
        assertEquals(entity.getDataContentType(), cloudEvent.getDataContentType());
        assertEquals(entity.getData().getMessage(), cloudEvent.getEventData().getMessage());
        assertEquals(entity.getData().getFieldsImpacted(), cloudEvent.getEventData().getFieldsImpacted());
        assertEquals(entity.getData().getRelatedObjects(), cloudEvent.getEventData().getRelatedObjects());
        assertEquals(entity.getProcessingStatus(), cloudEvent.getProcessingStatus());
    }

    @Test
    void modelToEntity() {
        CloudSystemEvent model = ModelTestFactory.buildCloudEventModel();
        final CloudSystemEventEntity entity = mapper.toCloudEventEntity(model);

        assertEquals(model.getSpecVersion(), entity.getSpecVersion());
        assertEquals(model.getType(), entity.getType());
        assertEquals(model.getSource(), entity.getSource());
        assertEquals(model.getSubject(), entity.getSubject());
        assertEquals(model.getTime(), entity.getTime());
        assertEquals(model.getRelatedProcess(), entity.getRelatedProcess());
        assertEquals(model.getDataContentType(), entity.getDataContentType());
        assertEquals(model.getEventData().getMessage(), entity.getData().getMessage());
        assertEquals(model.getEventData().getFieldsImpacted(), entity.getData().getFieldsImpacted());
        assertEquals(model.getEventData().getRelatedObjects(), entity.getData().getRelatedObjects());
        assertEquals(model.getProcessingStatus(), entity.getProcessingStatus());
    }

    @Test
    void modelToEntity_noIdAssigned() {
        CloudSystemEvent model = new CloudSystemEvent();
        final SystemEventData eventData = new SystemEventData();
        model.setEventData(eventData);
        final CloudSystemEventEntity entity = mapper.toCloudEventEntity(model);

        assertFalse(entity.getId().isBlank());
        assertNotNull(entity.getData());
        assertEquals(entity.getId(), entity.getData().getEventDataId());
    }

    @Test
    void modelToEntity_noIdAssigned_onCreationModel() {
        IntegrationCloudEvent model = new IntegrationCloudEvent();
        final CloudEventMetadata metadata = new CloudEventMetadata();
        metadata.setSource(URI.create("http://localhost:8000"));
        metadata.setTime(LocalDateTime.now());
        final SystemEventData data = new SystemEventData();
        data.setMessage("testMessage");
        model.setMetadata(metadata);
        model.setData(data);
        final CloudSystemEventEntity entity = mapper.toCloudEventEntity(model);

        assertFalse(entity.getId().isBlank());
        assertNotNull(entity.getData());
        assertEquals(entity.getId(), entity.getData().getEventDataId());
    }

    @Test
    void modelToEntity_recordCreationModel() {
        IntegrationCloudEvent model = ModelTestFactory.createIntegrationRecord();
        final CloudSystemEventEntity entity = mapper.toCloudEventEntity(model);

        assertEquals(model.getMetadata().getSpecVersion(), entity.getSpecVersion());
        assertEquals(model.getMetadata().getType(), entity.getType());
        assertEquals(model.getMetadata().getSource().toString(), entity.getSource());
        assertEquals(model.getMetadata().getSubject(), entity.getSubject());
        assertEquals(model.getMetadata().getTime(), entity.getTime());
        assertEquals(model.getMetadata().getRelatedProcess(), entity.getRelatedProcess());
        assertEquals(model.getMetadata().getDataContentType(), entity.getDataContentType());
        assertEquals(model.getData().getMessage(), entity.getData().getMessage());
        assertEquals(model.getData().getFieldsImpacted(), entity.getData().getFieldsImpacted());
        assertEquals(model.getData().getRelatedObjects(), entity.getData().getRelatedObjects());
    }

    @Test
    void modelToJson() {
        CloudSystemEvent model = ModelTestFactory.buildCloudEventModel();
        String json = mapper.toJson(model);

        assertNotNull(json);
        assertFalse(json.isEmpty());
    }
}
