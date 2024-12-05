package com.rentals.controllers;

import com.rentals.dto.messages.CreateMessageDto;
import com.rentals.responses.MessageResponse;
import com.rentals.services.MessageService;
import com.rentals.exceptions.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages management", description = "Endpoints for managing and sending messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(
            summary = "Send a new message",
            description = "Allows the creation and sending of a new message. Returns the created message details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Authentication token was either missing, invalid or expired.",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @Valid @RequestBody CreateMessageDto createMessageDto) {
        try {
            MessageResponse response = messageService.createMessage(createMessageDto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | UnauthorizedException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("An unexpected error occurred while processing the message.");
        }
    }
}
