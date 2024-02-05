package com.intellecteu.onesource.integration.api.cloudevents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
class CloudSystemEventServiceTest {

    @Mock
    private CloudSystemEventRepositoryApi cloudEventRepository;

    private CloudSystemEventMapper cloudSystemEventMapper;

    private CloudSystemEventService service;

    @BeforeEach
    void setUp() {
        cloudSystemEventMapper = new CloudSystemEventMapperImpl();
        service = new CloudSystemEventService(cloudEventRepository, cloudSystemEventMapper);
    }

    @Test
    @DisplayName("Test get cloud events by id")
    void testGetCloudEventsById_shouldReturnPageCloudEvents() {
        String id = "testId";
        final CloudSystemEventEntity entity = createCloudEventEntity(id);
        final CloudSystemEvent expectedResponse = cloudSystemEventMapper.toCloudEvent(entity);

        when(cloudEventRepository.findById(id)).thenReturn(Optional.of(entity));

        CloudSystemEvent actual = service.getCloudEventById(id);

        assertEquals(expectedResponse, actual);
    }

    @Test
    @DisplayName("Test get all cloud events with id in path parameters")
    void testGetAllCloudEventsWithIdInParameters_shouldReturnPageCloudEvents() {
        String id = "testId";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.put("id", List.of(id));

        Pageable pageable = PageRequest.of(0, 20);
        final CloudSystemEventEntity entity = createCloudEventEntity(id);
        List<CloudSystemEventEntity> entities = List.of(entity);
        Page<CloudSystemEventEntity> dbResponse = new PageImpl<>(entities);

        final CloudSystemEvent event = cloudSystemEventMapper.toCloudEvent(entity);
        List<CloudSystemEvent> events = List.of(event);
        Page<CloudSystemEvent> expectedResponse = new PageImpl<>(events);

        when(cloudEventRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(dbResponse);

        final Page<CloudSystemEvent> actual = service.getCloudEvents(pageable, parameters);

        assertEquals(expectedResponse, actual);
    }

    @Test
    @DisplayName("Test get all cloud events without path parameters")
    void testGetAllCloudEventsWithoutPathParameters_shouldReturnPageCloudEvents() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        Pageable pageable = PageRequest.of(0, 20);
        final CloudSystemEventEntity entity = createCloudEventEntity("testId");
        List<CloudSystemEventEntity> entities = List.of(entity);
        Page<CloudSystemEventEntity> dbResponse = new PageImpl<>(entities);

        final CloudSystemEvent event = cloudSystemEventMapper.toCloudEvent(entity);
        List<CloudSystemEvent> events = List.of(event);
        Page<CloudSystemEvent> expectedResponse = new PageImpl<>(events);

        when(cloudEventRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(dbResponse);

        final Page<CloudSystemEvent> actual = service.getCloudEvents(pageable, parameters);

        assertEquals(expectedResponse, actual);
    }

    private CloudSystemEventEntity createCloudEventEntity(String id) {
        return CloudSystemEventEntity.builder()
            .id(id)
            .specVersion("1.0")
            .type("testType")
            .source("testSource")
            .subject("testSubject")
            .time(LocalDateTime.now())
            .relatedProcess("testRelatedProcess")
            .relatedSubProcess("testRelatedSubProcess")
            .dataContentType("testDataContentType")
            .data("""
                { "message": "testMessage" }
                """)
            .build();
    }
}
