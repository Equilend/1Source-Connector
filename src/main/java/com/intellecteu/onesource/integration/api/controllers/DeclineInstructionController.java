package com.intellecteu.onesource.integration.api.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.intellecteu.onesource.integration.api.declineinstructions.DeclineInstructionService;
import com.intellecteu.onesource.integration.api.dto.DeclineInstruction;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Valid
@RequestMapping("/v1/decline-instructions")
public class DeclineInstructionController {

    private final DeclineInstructionService declineInstructionService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<DeclineInstruction>> getDeclineInstructions(final Pageable pageable,
        @RequestParam(required = false) final MultiValueMap<String, String> parameters) {
        PageResponse<DeclineInstruction> contracts = declineInstructionService.getAllInstructions(pageable, parameters);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(contracts);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DeclineInstruction> createDeclineInstruction(
        @NotNull @RequestBody DeclineInstruction declineInstruction) {
        DeclineInstruction createdInstruction = declineInstructionService.createDeclineInstruction(declineInstruction);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdInstruction);
    }
}
