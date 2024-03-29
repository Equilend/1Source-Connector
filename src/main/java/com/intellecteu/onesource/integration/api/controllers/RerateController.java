package com.intellecteu.onesource.integration.api.controllers;

import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.api.dto.RerateDTO;
import com.intellecteu.onesource.integration.api.services.rerates.RerateApiMapper;
import com.intellecteu.onesource.integration.api.services.rerates.RerateApiService;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/rerates")
public class RerateController {

    private final RerateApiService rerateApiService;
    private final RerateApiMapper mapper;

    @GetMapping
    public ResponseEntity<PageResponse<RerateDTO>> getAll(Pageable pageable,
        @RequestParam(required = false) final MultiValueMap<String, String> parameters) {
        Page<Rerate> rerates = rerateApiService.getAll(pageable, parameters);
        return ResponseEntity.ok(new PageResponse<>(rerates.map(mapper::toDTO)));
    }
}
