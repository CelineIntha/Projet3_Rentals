package com.rentals.services;

import com.rentals.dto.messages.CreateMessageDto;
import com.rentals.model.Message;
import com.rentals.model.Rental;
import com.rentals.model.User;
import com.rentals.repository.MessageRepository;
import com.rentals.repository.RentalRepository;
import com.rentals.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository, RentalRepository rentalRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    public Message createMessage(CreateMessageDto createMessageDto) {
        if (createMessageDto.getUserId() == null || createMessageDto.getRentalId() == null || createMessageDto.getMessage() == null) {
            throw new IllegalArgumentException("Les champs userId, rentalId et message sont obligatoires.");
        }

        User user = userRepository.findById(createMessageDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + createMessageDto.getUserId()));

        Rental rental = rentalRepository.findById(createMessageDto.getRentalId())
                .orElseThrow(() -> new IllegalArgumentException("Location non trouvée avec l'ID : " + createMessageDto.getRentalId()));

        System.out.println("Utilisateur trouvé : " + user.getId());
        System.out.println("Location trouvée : " + rental.getId());

        Message message = new Message();
        message.setUser(user);
        message.setRental(rental);
        message.setMessage(createMessageDto.getMessage());

        return messageRepository.save(message);
    }
}
