package com.rentals.controllers;

import com.rentals.dto.messages.CreateMessageDto;
import com.rentals.model.Message;
import com.rentals.services.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/messages/")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody CreateMessageDto createMessageDto) {

        try {
            Message message = messageService.createMessage(createMessageDto);

            if (message.getRental() == null || message.getUser() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data");
            }

            return ResponseEntity.status(HttpStatus.OK).body("Message sent with success");

        } catch (IllegalArgumentException e) {
            System.out.println("Erreur de validation : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data");
        }

    }


    @GetMapping("test")
    public ResponseEntity<String> testRoute() {
        return ResponseEntity.ok("La route fonctionne !");
    }

}
