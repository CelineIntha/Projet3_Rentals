package com.rentals.services;

import com.rentals.exceptions.NotFoundException;
import com.rentals.model.User;
import com.rentals.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé avec l'ID : " + id));
    }
}
