package com.intellecteu.onesource.integration.api.cloudevents;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CloudSystemEventExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFoundEvent(EntityNotFoundException e) {
        return ResponseEntity
            .status(HttpStatusCode.valueOf(404))
            .build();
    }
}
