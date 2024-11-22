package com.rentals.dto.rentals;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class UpdateRentalDto {
    private String name;
    private BigDecimal surface;
    private BigDecimal price;
    private String picture;
    private String description;
}