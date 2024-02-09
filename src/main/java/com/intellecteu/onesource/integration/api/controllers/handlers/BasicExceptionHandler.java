package com.intellecteu.onesource.integration.api.controllers.handlers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class BasicExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFoundEvent(EntityNotFoundException e) {
        return ResponseEntity
            .status(HttpStatusCode.valueOf(404))
            .build();
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleNotFoundEvent(HttpClientErrorException e) {
        return ResponseEntity
            .status(e.getStatusCode())
            .body(e.getMessage());
    }

}
