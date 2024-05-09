package com.intellecteu.onesource.integration.api.controllers.handlers;

import com.intellecteu.onesource.integration.api.controllers.CloudSystemEventController;
import com.intellecteu.onesource.integration.api.controllers.ContractController;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.QueryParameterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice(assignableTypes = {CloudSystemEventController.class, ContractController.class})
public class BasicExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFoundEvent(EntityNotFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build();
    }

    @ExceptionHandler(QueryParameterException.class)
    public ResponseEntity<String> handleUnexpectedParameter(QueryParameterException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_ACCEPTABLE)
            .body(e.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleClientException(HttpClientErrorException e) {
        return ResponseEntity
            .status(e.getStatusCode())
            .body(e.getMessage());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> handleServerError(HttpServerErrorException e) {
        return ResponseEntity
            .status(e.getStatusCode())
            .body(e.getMessage());
    }

}
