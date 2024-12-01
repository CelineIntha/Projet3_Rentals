package com.rentals.controllers;

import com.rentals.exceptions.UnauthorizedException;
import com.rentals.exceptions.NotFoundException;
import com.rentals.model.User;
import com.rentals.dto.auth.LoginUserDto;
import com.rentals.dto.auth.RegisterUserDto;
import com.rentals.responses.ErrorResponse;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.Map;

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
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto loginUserDto) {
        logger.info("Attempting to authenticate user: {}", loginUserDto.getEmail());

        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        if (authenticatedUser == null) {
            logger.warn("Authentication failed for user: {}", loginUserDto.getEmail());
            throw new UnauthorizedException("Invalid email or password.");
        }

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setExpiresIn(jwtService.getExpirationTime());

        logger.info("Authentication successful for user: {}", loginUserDto.getEmail());
        return ResponseEntity.ok(loginResponse);
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
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        logger.info("Attempting to register user with email: {}", registerUserDto.getEmail());

        if (userService.findByEmail(registerUserDto.getEmail()) != null) {
            logger.warn("Registration failed: email already exists: {}", registerUserDto.getEmail());
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "An account with this email address already exists."
            ));
        }

        try {
            User registeredUser = authenticationService.signup(registerUserDto);

            String jwtToken = jwtService.generateToken(registeredUser);

            UserResponse userResponse = new UserResponse(
                    registeredUser.getId(),
                    registeredUser.getName(),
                    registeredUser.getEmail(),
                    registeredUser.getCreatedAt().format(dateFormatter),
                    registeredUser.getUpdatedAt().format(dateFormatter)
            );

            logger.info("User registered successfully with email: {}", registerUserDto.getEmail());

            return ResponseEntity.ok(Map.of(
                    "user", userResponse,
                    "token", jwtToken,
                    "expiresIn", jwtService.getExpirationTime()
            ));

        } catch (Exception e) {
            logger.error("Registration failed for email: {}", registerUserDto.getEmail(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "message", "An unexpected error occurred during registration."
            ));
        }
    }


    @GetMapping("/me")
    @Operation(
            summary = "Get your user information.",
            description = "Get your user information."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized: Authentication token was either missing, invalid or expired.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<UserResponse> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("User is not authenticated.");
            throw new UnauthorizedException("User is not authenticated.");
        }

        String email = authentication.getName();
        logger.info("Fetching authenticated user details for email: {}", email);

        User user = userService.findByEmail(email);

        if (user == null) {
            logger.error("User not found for email: {}", email);
            throw new NotFoundException("User not found for email: " + email);
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

        logger.info("Authenticated user details fetched successfully for email: {}", email);
        return ResponseEntity.ok(userResponse);
    }

}
