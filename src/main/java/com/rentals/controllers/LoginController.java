package com.rentals.controllers;

import com.rentals.model.User;
import com.rentals.dto.auth.LoginUserDto;
import com.rentals.dto.auth.RegisterUserDto;
import com.rentals.responses.LoginResponse;
import com.rentals.responses.UserResponse;
import com.rentals.services.AuthenticationService;
import com.rentals.services.JwtService;
import com.rentals.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

@RequestMapping("/api/auth")
@RestController
@Tag(name = "Authentication", description = "Endpoints for user authentication and account management")
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
    @Operation(
            summary = "Authenticate user",
            description = "Authenticate the user by email and password and return a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(mediaType = "application/json")
            )
    })
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
    @Operation(
            summary = "Register new user",
            description = "Create a new account for the user with the provided information."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Registration failed due to invalid data or existing account",
                    content = @Content(mediaType = "application/json")
            )
    })
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
    @Operation(
            summary = "Get authenticated user",
            description = "Retrieve the currently authenticated user's details."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authenticated user retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = "application/json")
            )
    })
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
