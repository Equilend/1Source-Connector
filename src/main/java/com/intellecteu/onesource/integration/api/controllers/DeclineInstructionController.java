package com.intellecteu.onesource.integration.api.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.intellecteu.onesource.integration.api.dto.DeclineInstructionDto;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionApiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Valid
@RequestMapping("/v1/decline-instructions")
public class DeclineInstructionController {

    private final DeclineInstructionApiService declineInstructionApiService;

    @CrossOrigin
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<DeclineInstructionDto>> getDeclineInstructions(final Pageable pageable,
        @RequestParam(required = false) final MultiValueMap<String, String> parameters) {
        PageResponse<DeclineInstructionDto> contracts = declineInstructionApiService.getAllInstructions(pageable,
            parameters);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(contracts);
    }

    @CrossOrigin
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DeclineInstructionDto> createDeclineInstruction(
        @NotNull @RequestBody DeclineInstructionDto declineInstructionDto) {
        DeclineInstructionDto createdInstruction = declineInstructionApiService.createDeclineInstruction(
            declineInstructionDto);
        return ResponseEntity
            .accepted()
            .body(createdInstruction);
    }
}
