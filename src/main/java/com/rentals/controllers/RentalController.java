package com.rentals.controllers;

import com.rentals.model.Rental;
import com.rentals.model.User;
import com.rentals.responses.RentalResponse;
import com.rentals.services.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Rentals management", description = "Endpoints for managing rentals")
public class RentalController {

    private static final Logger logger = LoggerFactory.getLogger(RentalController.class);
    private final RentalService rentalService;
    private final Path uploadDir = Paths.get("uploads");

    @Value("${base.url}")
    private String baseUrl;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Operation(summary = "Get all rentals", description = "Retrieve a list of all rentals with details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of rentals retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Map<String, List<RentalResponse>>> getAllRentals() {
        List<Rental> rentals = rentalService.findAllRentals();
        List<RentalResponse> response = rentals.stream().map(rental -> new RentalResponse(
                rental.getId(),
                rental.getName(),
                rental.getSurface(),
                rental.getPrice(),
                rental.getPicture(),
                rental.getDescription(),
                rental.getOwner().getId(),
                rental.getCreatedAt() != null ? rental.getCreatedAt().format(dateFormatter) : null,
                rental.getUpdatedAt() != null ? rental.getUpdatedAt().format(dateFormatter) : null
        )).toList();
        return ResponseEntity.ok(Map.of("rentals", response));
    }

    @Operation(summary = "Get rental by ID", description = "Retrieve a rental by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Rental not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> getRentalById(@PathVariable Integer id) {
        Rental rental = rentalService.findRentalById(id);
        if (rental == null) {
            return ResponseEntity.notFound().build();
        }
        RentalResponse response = new RentalResponse(
                rental.getId(),
                rental.getName(),
                rental.getSurface(),
                rental.getPrice(),
                rental.getPicture(),
                rental.getDescription(),
                rental.getOwner().getId(),
                rental.getCreatedAt() != null ? rental.getCreatedAt().format(dateFormatter) : null,
                rental.getUpdatedAt() != null ? rental.getUpdatedAt().format(dateFormatter) : null
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Retrieve an image", description = "Fetch an image by its filename.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = uploadDir.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            logger.error("Error fetching image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Create a new rental", description = "Add a new rental to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental created successfully"),
            @ApiResponse(responseCode = "500", description = "Error while creating rental", content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> createRental(
            @RequestParam("name") String name,
            @RequestParam("surface") BigDecimal surface,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            @RequestParam("description") String description
    ) {
        try {
            String pictureUrl = null;

            if (picture != null && !picture.isEmpty()) {
                if (Files.notExists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                Path imagePath = uploadDir.resolve(Objects.requireNonNull(picture.getOriginalFilename()));
                Files.copy(picture.getInputStream(), imagePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                pictureUrl = baseUrl + "/api/rentals/images/" + picture.getOriginalFilename();
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal();

            Rental rental = new Rental();
            rental.setName(name);
            rental.setSurface(surface);
            rental.setPrice(price);
            rental.setPicture(pictureUrl);
            rental.setDescription(description);
            rental.setOwner(authenticatedUser);
            rental.setCreatedAt(LocalDateTime.now());
            rental.setUpdatedAt(LocalDateTime.now());

            rentalService.createRental(rental);

            return ResponseEntity.status(HttpStatus.OK).body("Rental created");

        } catch (IOException e) {
            logger.error("Error while uploading picture: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while uploading picture");
        } catch (Exception e) {
            logger.error("Error while creating rental: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while creating rental");
        }
    }

    @Operation(summary = "Update a rental", description = "Update the details of an existing rental by his id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental updated successfully"),
            @ApiResponse(responseCode = "404", description = "Rental not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> updateRental(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("surface") BigDecimal surface,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description
    ) {
        Rental updateRental = rentalService.findRentalById(id);
        if (updateRental == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rental not found");
        }

        updateRental.setName(name);
        updateRental.setSurface(surface);
        updateRental.setPrice(price);
        updateRental.setDescription(description);
        updateRental.setUpdatedAt(LocalDateTime.now());

        rentalService.updateRental(updateRental);

        return ResponseEntity.status(HttpStatus.OK).body("Rental updated!");
    }
}
