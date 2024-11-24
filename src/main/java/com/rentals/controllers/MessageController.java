package com.rentals.controllers;

import com.rentals.dto.messages.CreateMessageDto;
import com.rentals.responses.MessageResponse;
import com.rentals.services.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody CreateMessageDto createMessageDto) {
        try {
            MessageResponse response = messageService.createMessage(createMessageDto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", ex);
        }
    }
}
