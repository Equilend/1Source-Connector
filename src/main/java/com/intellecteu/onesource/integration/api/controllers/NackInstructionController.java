package com.intellecteu.onesource.integration.api.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.intellecteu.onesource.integration.api.dto.NackInstructionDTO;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.api.services.nackinstructions.NackInstructionApiMapper;
import com.intellecteu.onesource.integration.api.services.nackinstructions.NackInstructionApiService;
import com.intellecteu.onesource.integration.model.integrationtoolkit.NackInstruction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
@RequestMapping("/v1/nack-instructions")
public class NackInstructionController {

    private final NackInstructionApiService nackInstructionApiService;
    private final NackInstructionApiMapper mapper;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<NackInstructionDTO>> getNackInstructions(Pageable pageable,
        @RequestParam(required = false) final MultiValueMap<String, String> parameters) {
        Page<NackInstruction> nackInstructions = nackInstructionApiService.getNackInstructions(pageable, parameters);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new PageResponse<>(nackInstructions.map(mapper::toDTO)));
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<NackInstructionDTO> createNackInstruction(
        @NotNull @RequestBody NackInstructionDTO nackInstructionDTO) {
        NackInstructionDTO nackInstruction = mapper.toDTO(
            nackInstructionApiService.save(mapper.toModel(nackInstructionDTO)));
        return ResponseEntity
            .accepted()
            .body(nackInstruction);
    }
}
