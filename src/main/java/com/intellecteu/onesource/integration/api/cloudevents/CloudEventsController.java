package com.intellecteu.onesource.integration.api.cloudevents;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/cloudevents")
public class CloudEventsController {

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCloudEvents() {
        String answer = """
            {
                "testKey": "testValue"
            }
            """;
        return ResponseEntity.of(Optional.of(answer));
    }

}
