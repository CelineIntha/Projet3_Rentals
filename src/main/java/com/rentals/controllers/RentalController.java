package com.rentals.controllers;

import com.rentals.model.Rental;
import com.rentals.model.User;
import com.rentals.responses.RentalResponse;
import com.rentals.services.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private static final Logger logger = LoggerFactory.getLogger(RentalController.class);
    private final RentalService rentalService;
    private final Path uploadDir = Paths.get("uploads");

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

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
                rental.getOwnerId(),
                rental.getCreatedAt() != null ? rental.getCreatedAt().toString() : null,
                rental.getUpdatedAt() != null ? rental.getUpdatedAt().toString() : null
        )).toList();
        return ResponseEntity.ok(Map.of("rentals", response));
    }

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
                rental.getOwnerId(),
                rental.getCreatedAt() != null ? rental.getCreatedAt().toString() : null,
                rental.getUpdatedAt() != null ? rental.getUpdatedAt().toString() : null
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RentalResponse> createRental(
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
                if (Files.exists(imagePath)) {
                    logger.warn("File already exists: {}. It will be overwritten.", imagePath);
                }
                Files.copy(picture.getInputStream(), imagePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                pictureUrl = "/api/rentals/images/" + picture.getOriginalFilename();
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal();
            Integer ownerId = authenticatedUser.getId();

            Rental rental = new Rental();
            rental.setName(name);
            rental.setSurface(surface);
            rental.setPrice(price);
            rental.setPicture(pictureUrl);
            rental.setDescription(description);
            rental.setOwnerId(ownerId);
            rental.setCreatedAt(LocalDateTime.now());
            rental.setUpdatedAt(LocalDateTime.now());

            Rental savedRental = rentalService.createRental(rental);

            RentalResponse response = new RentalResponse(
                    savedRental.getId(),
                    savedRental.getName(),
                    savedRental.getSurface(),
                    savedRental.getPrice(),
                    savedRental.getPicture(),
                    savedRental.getDescription(),
                    savedRental.getOwnerId(),
                    savedRental.getCreatedAt() != null ? savedRental.getCreatedAt().toString() : null,
                    savedRental.getUpdatedAt() != null ? savedRental.getUpdatedAt().toString() : null
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            logger.error("Error while uploading picture: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            logger.error("Error while creating rental: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RentalResponse> updateRental(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("surface") BigDecimal surface,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description
    ) {
        Rental existingRental = rentalService.findRentalById(id);
        if (existingRental == null) {
            return ResponseEntity.notFound().build();
        }

        existingRental.setName(name);
        existingRental.setSurface(surface);
        existingRental.setPrice(price);
        existingRental.setDescription(description);
        existingRental.setUpdatedAt(LocalDateTime.now());

        Rental updatedRental = rentalService.updateRental(existingRental);

        RentalResponse response = new RentalResponse(
                updatedRental.getId(),
                updatedRental.getName(),
                updatedRental.getSurface(),
                updatedRental.getPrice(),
                updatedRental.getPicture(),
                updatedRental.getDescription(),
                updatedRental.getOwnerId(),
                updatedRental.getCreatedAt() != null ? updatedRental.getCreatedAt().toString() : null,
                updatedRental.getUpdatedAt() != null ? updatedRental.getUpdatedAt().toString() : null
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<?> getImage(@PathVariable String filename) {
        try {
            Path filePath = uploadDir.resolve(filename).normalize();
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header("Content-Type", mimeType)
                    .body(Files.readAllBytes(filePath));
        } catch (IOException e) {
            logger.error("Error while reading image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to read image");
        }
    }
}
