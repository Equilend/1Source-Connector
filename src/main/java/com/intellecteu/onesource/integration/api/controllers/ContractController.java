package com.intellecteu.onesource.integration.api.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.intellecteu.onesource.integration.api.dto.ContractDto;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.api.services.contracts.ContractApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Valid
@RequestMapping("/v1/contracts")
public class ContractController {

    private final ContractApiService contractService;

    @CrossOrigin
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<ContractDto>> getContracts(final Pageable pageable,
        @RequestParam(required = false) final MultiValueMap<String, String> parameters) {
        PageResponse<ContractDto> contracts = contractService.getAllContracts(pageable, parameters);
        return ResponseEntity
            .status(200)
            .body(contracts);
    }

    @CrossOrigin
    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ContractDto> getContractById(@NonNull @PathVariable final String id) {
        ContractDto contract = contractService.getContractById(id);
        return ResponseEntity
            .status(200)
            .body(contract);
    }
}
