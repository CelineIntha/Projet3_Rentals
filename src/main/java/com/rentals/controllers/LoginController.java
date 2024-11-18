package com.rentals.controllers;

import com.rentals.model.User;
import com.rentals.dto.LoginUserDto;
import com.rentals.dto.RegisterUserDto;
import com.rentals.responses.LoginResponse;
import com.rentals.responses.UserResponse;
import com.rentals.services.AuthenticationService;
import com.rentals.services.JwtService;
import com.rentals.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

@RequestMapping("/api/auth")
@RestController
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public LoginController(JwtService jwtService, AuthenticationService authenticationService, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginUserDto loginUserDto) {
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);

            String jwtToken = jwtService.generateToken(authenticatedUser);

            LoginResponse loginResponse = new LoginResponse()
                    .setToken(jwtToken)
                    .setExpiresIn(jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);

        } catch (RuntimeException e) {
            logger.error("Authentication failed");
            return ResponseEntity.status(401).body("Authentication failed. Please check your credentials and try again.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        if (userService.findByEmail(registerUserDto.getEmail()) != null) {
            logger.error("Account already exists");
            return ResponseEntity.status(400).body("An account with this email address already exists. Please log in or use a different email address to register.");
        }

        try {
            User registeredUser = authenticationService.signup(registerUserDto);

            String formattedCreatedAt = registeredUser.getCreatedAt().format(dateFormatter);
            String formattedUpdatedAt = registeredUser.getUpdatedAt().format(dateFormatter);

            UserResponse userResponse = new UserResponse(
                    registeredUser.getId(),
                    registeredUser.getName(),
                    registeredUser.getEmail(),
                    formattedCreatedAt,
                    formattedUpdatedAt
            );

            return ResponseEntity.ok(userResponse);

        } catch (RuntimeException e) {
            logger.error("Registration failed");
            return ResponseEntity.status(400).body("Registration failed. Please verify the information and try again.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userService.findByEmail(email);

        if (user == null) {
            logger.error("User not found for email: {}", email);
            return ResponseEntity.status(404).body("error:User not found");
        }

        String formattedCreatedAt = user.getCreatedAt().format(dateFormatter);
        String formattedUpdatedAt = user.getUpdatedAt().format(dateFormatter);

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                formattedCreatedAt,
                formattedUpdatedAt
        );

        return ResponseEntity.ok(userResponse);
    }
}
