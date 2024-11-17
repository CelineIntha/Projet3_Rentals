package com.rentals.controllers;

import com.rentals.model.Rental;
import com.rentals.responses.RentalResponse;
import com.rentals.services.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

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

}
