package com.intellecteu.onesource.integration.api.services.cloudevents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.intellecteu.onesource.integration.api.dto.CloudSystemEventDto;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
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

    private CloudSystemEventApiMapper cloudSystemEventMapper;

    private CloudSystemEventService service;

    @BeforeEach
    void setUp() {
        cloudSystemEventMapper = Mappers.getMapper(CloudSystemEventApiMapper.class);
        service = new CloudSystemEventService(cloudEventRepository, cloudSystemEventMapper);
    }

    @Test
    @DisplayName("Test get cloud events by id")
    void testGetCloudEventsById_shouldReturnPageCloudEvents() {
        String id = "testId";
        final CloudSystemEventEntity entity = createCloudEventEntity(id);
        final CloudSystemEventDto expectedResponse = cloudSystemEventMapper.toCloudEventDto(entity);

        when(cloudEventRepository.findById(id)).thenReturn(Optional.of(entity));

        CloudSystemEventDto actual = service.getCloudEventById(id);

        assertEquals(expectedResponse, actual);
    }

    @Test
    @DisplayName("Test get all cloud events with id in path parameters")
    void testGetAllCloudEventsWithIdInParameters_shouldReturnPageResponse() {
        String id = "testId";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.put("id", List.of(id));

        Pageable pageable = PageRequest.of(0, 20);
        final CloudSystemEventEntity entity = createCloudEventEntity(id);
        List<CloudSystemEventEntity> entities = List.of(entity);
        Page<CloudSystemEventEntity> dbResponse = new PageImpl<>(entities, pageable, 20);

        final CloudSystemEventDto event = cloudSystemEventMapper.toCloudEventDto(entity);
        List<CloudSystemEventDto> events = List.of(event);
        Page<CloudSystemEventDto> expectedResponse = new PageImpl<>(events, pageable, 20);

        when(cloudEventRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(dbResponse);

        final PageResponse<CloudSystemEventDto> actual = service.getCloudEvents(pageable, parameters);

        assertEquals(expectedResponse.getTotalElements(), actual.getTotalItems());
        assertEquals(expectedResponse.getTotalPages(), actual.getTotalPages());
        assertEquals(expectedResponse.getPageable().getPageNumber(), actual.getCurrentPage());
        assertEquals(expectedResponse.getContent(), actual.getItems());
    }

    @Test
    @DisplayName("Test get all cloud events without path parameters")
    void testGetAllCloudEventsWithoutPathParameters_shouldReturnPageResponse() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        Pageable pageable = PageRequest.of(0, 20);
        final CloudSystemEventEntity entity = createCloudEventEntity("testId");
        List<CloudSystemEventEntity> entities = List.of(entity);
        Pageable defaultPage = PageRequest.of(0, 20);
        Page<CloudSystemEventEntity> dbResponse = new PageImpl<>(entities, defaultPage, 20);

        final CloudSystemEventDto event = cloudSystemEventMapper.toCloudEventDto(entity);
        List<CloudSystemEventDto> events = List.of(event);
        Page<CloudSystemEventDto> expectedResponse = new PageImpl<>(events, defaultPage, 20);

        when(cloudEventRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(dbResponse);

        final PageResponse<CloudSystemEventDto> actual = service.getCloudEvents(pageable, parameters);

        assertEquals(expectedResponse.getTotalElements(), actual.getTotalItems());
        assertEquals(expectedResponse.getTotalPages(), actual.getTotalPages());
        assertEquals(expectedResponse.getPageable().getPageNumber(), actual.getCurrentPage());
        assertEquals(expectedResponse.getContent(), actual.getItems());
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
            .data(null)
            .build();
    }
}
