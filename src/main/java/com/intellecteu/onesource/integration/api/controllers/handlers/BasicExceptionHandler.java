package com.intellecteu.onesource.integration.api.controllers.handlers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice
public class BasicExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFoundEvent(EntityNotFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build();
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleNotFoundEvent(HttpClientErrorException e) {
        return ResponseEntity
            .status(e.getStatusCode())
            .body(e.getMessage());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> handleNotFoundEvent(HttpServerErrorException e) {
        return ResponseEntity
            .status(e.getStatusCode())
            .body(e.getMessage());
    }

}
