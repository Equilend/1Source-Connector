package com.intellecteu.onesource.integration.api.cloudevents;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Valid
@RequestMapping("/v1")
public class CloudSystemEventController {

    private final CloudSystemEventService cloudSystemEventService;

    @GetMapping(path = "/cloudevents", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CloudSystemEvent>> getCloudEventById(final Pageable pageable,
        @RequestParam(required = false) final MultiValueMap<String, String> parameters) {
        Page<CloudSystemEvent> events = cloudSystemEventService.getCloudEvents(pageable, parameters);
        return ResponseEntity
            .status(200)
            .body(events);
    }

    @GetMapping(path = "/cloudevent/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CloudSystemEvent> getCloudEventById(@NonNull @PathVariable final String id) {
        CloudSystemEvent event = cloudSystemEventService.getCloudEventById(id);
        return ResponseEntity
            .status(200)
            .body(event);
    }
}
