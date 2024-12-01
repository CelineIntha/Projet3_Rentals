package com.rentals.dto.rentals;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
public class RentalDto {
    private String name;
    private BigDecimal surface;
    private BigDecimal price;
    private String description;
    private MultipartFile picture;
}
