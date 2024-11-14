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

@RequestMapping("/api/auth")
@RestController
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

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
            logger.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        try {
            User registeredUser = authenticationService.signup(registerUserDto);

            UserResponse userResponse = new UserResponse(registeredUser.getId(), registeredUser.getName(), registeredUser.getEmail());

            return ResponseEntity.ok(userResponse);

        } catch (RuntimeException e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(401).body("Utilisateur non authentifié");
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(404).body("Utilisateur non trouvé");
        }

        UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getEmail());

        return ResponseEntity.ok(userResponse);
    }
}
