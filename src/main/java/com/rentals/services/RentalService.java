package com.rentals.services;

import com.rentals.model.Rental;
import com.rentals.repository.RentalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentalService {

    private static final Logger logger = LoggerFactory.getLogger(RentalService.class);
    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public List<Rental> findAllRentals() {
        logger.info("Fetching all rentals from the database");
        return (List<Rental>) rentalRepository.findAll();
    }

    public Rental findRentalById(Integer id) {
        logger.info("Fetching rental with ID: {}", id);
        return rentalRepository.findById(id).orElse(null);
    }

}
