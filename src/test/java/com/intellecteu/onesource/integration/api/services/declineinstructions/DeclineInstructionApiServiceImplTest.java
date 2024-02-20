package com.intellecteu.onesource.integration.api.services.declineinstructions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.intellecteu.onesource.integration.api.EntityApiTestFactory;
import com.intellecteu.onesource.integration.api.dto.DeclineInstructionDto;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import java.util.List;
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
class DeclineInstructionApiServiceImplTest {

    @Mock
    private DeclineInstructionApiRepository declineInstructionRepository;

    private DeclineInstructionApiMapper mapper;

    private DeclineInstructionApiService service;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(DeclineInstructionApiMapper.class);
        service = new DeclineInstructionApiServiceImpl(declineInstructionRepository, mapper);
    }

    @Test
    @DisplayName("Test get all decline instructions with declineInstructionId in path parameters")
    void testGetAllDeclineInstructionsWithDeclineInstructionIdInParameters_shouldReturnPageResponse() {
        String id = "declineInstructionId";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.put("declineInstructionId", List.of(id));

        Pageable pageable = PageRequest.of(0, 20);
        final DeclineInstructionEntity entity = EntityApiTestFactory.buildDeclineInstructionEntity(id);
        List<DeclineInstructionEntity> entities = List.of(entity);
        Page<DeclineInstructionEntity> dbResponse = new PageImpl<>(entities, pageable, 20);

        final DeclineInstructionDto event = mapper.toDto(entity);
        List<DeclineInstructionDto> events = List.of(event);
        Page<DeclineInstructionDto> expectedResponse = new PageImpl<>(events, pageable, 20);

        when(declineInstructionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(dbResponse);

        final PageResponse<DeclineInstructionDto> actual = service.getAllInstructions(pageable, parameters);

        assertEquals(expectedResponse.getTotalElements(), actual.getTotalItems());
        assertEquals(expectedResponse.getTotalPages(), actual.getTotalPages());
        assertEquals(expectedResponse.getPageable().getPageNumber(), actual.getCurrentPage());
        assertEquals(expectedResponse.getContent(), actual.getItems());
    }

    @Test
    @DisplayName("Test get all decline instructions without parameters")
    void testGetAllDeclineInstructionsWithoutParameters_shouldReturnPageResponse() {
        String id = "declineInstructionId";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        Pageable pageable = PageRequest.of(0, 20);
        final DeclineInstructionEntity entity = EntityApiTestFactory.buildDeclineInstructionEntity(id);
        List<DeclineInstructionEntity> entities = List.of(entity);
        Page<DeclineInstructionEntity> dbResponse = new PageImpl<>(entities, pageable, 20);

        final DeclineInstructionDto event = mapper.toDto(entity);
        List<DeclineInstructionDto> events = List.of(event);
        Page<DeclineInstructionDto> expectedResponse = new PageImpl<>(events, pageable, 20);

        when(declineInstructionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(dbResponse);

        final PageResponse<DeclineInstructionDto> actual = service.getAllInstructions(pageable, parameters);

        assertEquals(expectedResponse.getTotalElements(), actual.getTotalItems());
        assertEquals(expectedResponse.getTotalPages(), actual.getTotalPages());
        assertEquals(expectedResponse.getPageable().getPageNumber(), actual.getCurrentPage());
        assertEquals(expectedResponse.getContent(), actual.getItems());
    }

    @Test
    @DisplayName("Test create decline instruction")
    void testCreateDeclineInstruction_shouldReturnDeclineInstruction() {
        String id = "declineInstructionId";
        final DeclineInstructionEntity entity = EntityApiTestFactory.buildDeclineInstructionEntity(id);
        final DeclineInstructionDto dto = mapper.toDto(entity);

        when(declineInstructionRepository.save(any(DeclineInstructionEntity.class))).thenReturn(entity);

        final DeclineInstructionDto actual = service.createDeclineInstruction(dto);

        assertEquals(dto, actual);
    }


}