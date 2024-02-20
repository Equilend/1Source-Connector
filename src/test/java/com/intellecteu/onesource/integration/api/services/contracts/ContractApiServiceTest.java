package com.intellecteu.onesource.integration.api.services.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.intellecteu.onesource.integration.api.EntityApiTestFactory;
import com.intellecteu.onesource.integration.api.dto.ContractDto;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
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
class ContractApiServiceTest {

    @Mock
    private ContractApiRepository contractRepository;
    private ContractApiMapper mapper;

    private ContractApiService service;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ContractApiMapper.class);
        service = new ContractApiService(contractRepository, mapper);
    }

    @Test
    @DisplayName("Test get contracts by id")
    void testGetContractsById_shouldReturnContract() {
        String contractId = "testContractId";
        final ContractEntity entity = EntityApiTestFactory.buildContract(contractId);
        final ContractDto expectedResponse = mapper.toDto(entity);

        when(contractRepository.getByContractId(contractId)).thenReturn(Optional.of(entity));

        ContractDto actual = service.getContractById(contractId);

        assertEquals(expectedResponse.getContractId(), actual.getContractId());
        assertEquals(expectedResponse.getTrade().getId(), actual.getTrade().getId());
        assertEquals(expectedResponse.getTrade().getVenue().getId(), actual.getTrade().getVenue().getId());
    }

    @Test
    @DisplayName("Test get all contracts with contractId in path parameters")
    void testGetAllContractsWithContractIdInParameters_shouldReturnPageResponse() {
        String contractId = "testId";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.put("contractId", List.of(contractId));

        Pageable pageable = PageRequest.of(0, 20);
        final ContractEntity entity = EntityApiTestFactory.buildContract(contractId);
        List<ContractEntity> entities = List.of(entity);
        Page<ContractEntity> dbResponse = new PageImpl<>(entities, pageable, 20);

        final ContractDto event = mapper.toDto(entity);
        List<ContractDto> events = List.of(event);
        Page<ContractDto> expectedResponse = new PageImpl<>(events, pageable, 20);

        when(contractRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(dbResponse);

        final PageResponse<ContractDto> actual = service.getAllContracts(pageable, parameters);

        assertEquals(expectedResponse.getTotalElements(), actual.getTotalItems());
        assertEquals(expectedResponse.getTotalPages(), actual.getTotalPages());
        assertEquals(expectedResponse.getPageable().getPageNumber(), actual.getCurrentPage());
        assertEquals(expectedResponse.getContent().get(0).getContractId(), actual.getItems().get(0).getContractId());
    }

    @Test
    @DisplayName("Test get all contracts without path parameters")
    void testGetAllContractsWithoutParameters_shouldReturnPageResponse() {
        String contractId = "testId";
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        Pageable pageable = PageRequest.of(0, 20);
        final ContractEntity entity = EntityApiTestFactory.buildContract(contractId);
        List<ContractEntity> entities = List.of(entity);
        Page<ContractEntity> dbResponse = new PageImpl<>(entities, pageable, 20);

        final ContractDto event = mapper.toDto(entity);
        List<ContractDto> events = List.of(event);
        Page<ContractDto> expectedResponse = new PageImpl<>(events, pageable, 20);

        when(contractRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(dbResponse);

        final PageResponse<ContractDto> actual = service.getAllContracts(pageable, parameters);

        assertEquals(expectedResponse.getTotalElements(), actual.getTotalItems());
        assertEquals(expectedResponse.getTotalPages(), actual.getTotalPages());
        assertEquals(expectedResponse.getPageable().getPageNumber(), actual.getCurrentPage());
        assertEquals(expectedResponse.getContent().get(0).getContractId(), actual.getItems().get(0).getContractId());
    }


}