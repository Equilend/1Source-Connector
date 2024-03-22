package com.intellecteu.onesource.integration.api.services.rerates;

import static com.intellecteu.onesource.integration.api.services.rerates.RerateFilterFields.RERATE_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.openMocks;

import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.mapper.OneSourceMapperImpl;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.repository.entity.onesource.RerateEntity;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class RerateApiServiceTest {

    @Mock
    private RerateApiRepository rerateApiRepository;
    private OneSourceMapper oneSourceMapper = new OneSourceMapperImpl();

    private RerateApiService rerateApiService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        rerateApiService = new RerateApiService(rerateApiRepository, oneSourceMapper);
    }

    @Test
    void getAll_CorrectRequestAndExistingRerate_PageWithRerates() {
        String rerateId = "id_of_rerate";
        Pageable pageable = PageRequest.of(0, 10);
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add(RERATE_ID, rerateId);
        RerateEntity rerateEntity = new RerateEntity();
        rerateEntity.setRerateId(rerateId);
        PageImpl page = new PageImpl<>(List.of(rerateEntity));
        doReturn(page).when(rerateApiRepository).findAll(any(), any(Pageable.class));

        Page<Rerate> result = rerateApiService.getAll(pageable, parameters);

        assertEquals(1, result.getTotalElements());
    }
}