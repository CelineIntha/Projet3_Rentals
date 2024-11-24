package com.rentals.services;

import com.rentals.dto.messages.CreateMessageDto;
import com.rentals.responses.MessageResponse;
import com.rentals.model.Message;
import com.rentals.model.Rental;
import com.rentals.model.User;
import com.rentals.repository.MessageRepository;
import com.rentals.repository.RentalRepository;
import com.rentals.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, RentalRepository rentalRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    public MessageResponse createMessage(CreateMessageDto createMessageDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();

        if (!authenticatedUser.getId().equals(createMessageDto.getUserId())) {
            throw new IllegalArgumentException("User ID in the request does not match the authenticated user.");
        }

        Rental rental = rentalRepository.findById(createMessageDto.getRentalId())
                .orElseThrow(() -> new IllegalArgumentException("Rental not found with id: " + createMessageDto.getRentalId()));

        if (!rental.getOwner().getId().equals(authenticatedUser.getId())) {
            throw new SecurityException("You do not have permission to send a message for this rental.");
        }

        Message message = new Message();
        message.setRental(rental);
        message.setUser(authenticatedUser);
        message.setMessage(createMessageDto.getMessage());
        messageRepository.save(message);

        return new MessageResponse("Message sent successfully.");
    }
}
