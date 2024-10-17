package com.intellecteu.onesource.integration.api.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.intellecteu.onesource.integration.api.dto.CloudSystemEventDto;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventService;
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
@RequestMapping("/v1/cloudevents")
public class CloudSystemEventController {

    private final CloudSystemEventService cloudSystemEventService;

    @CrossOrigin
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<CloudSystemEventDto>> getCloudEvents(final Pageable pageable,
        @RequestParam(required = false) final MultiValueMap<String, String> parameters) {
        PageResponse<CloudSystemEventDto> events = cloudSystemEventService.getCloudEvents(pageable, parameters);
        return ResponseEntity
            .status(200)
            .body(events);
    }

    @CrossOrigin
    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CloudSystemEventDto> getCloudEventById(@NonNull @PathVariable final String id) {
        CloudSystemEventDto event = cloudSystemEventService.getCloudEventById(id);
        return ResponseEntity
            .status(200)
            .body(event);
    }

    @CrossOrigin
    @GetMapping(path = "/report", produces = APPLICATION_JSON_VALUE)
    public String getCloudEventsReport() {
        return "Under construction";
    }
}
