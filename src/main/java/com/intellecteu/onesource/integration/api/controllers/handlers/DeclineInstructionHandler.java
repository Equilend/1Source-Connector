package com.intellecteu.onesource.integration.api.controllers.handlers;

import com.intellecteu.onesource.integration.api.controllers.DeclineInstructionController;
import com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionCreationException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = DeclineInstructionController.class)
public class DeclineInstructionHandler {

    @ExceptionHandler(DeclineInstructionCreationException.class)
    public ResponseEntity<String> handleNotAcceptableInstruction(DeclineInstructionCreationException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_ACCEPTABLE)
            .body(e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFoundEntity(EntityNotFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleNotManagedException(RuntimeException e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(e.getMessage());
    }

}
