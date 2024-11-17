package com.rentals.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CreateRentalDto {

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotNull(message = "La surface est obligatoire")
    private BigDecimal surface;

    @NotNull(message = "Le prix est obligatoire")
    private BigDecimal price;

    private String picture;

    private String description;

    @NotNull(message = "L'ID du propriétaire est obligatoire")
    private Integer ownerId;
}