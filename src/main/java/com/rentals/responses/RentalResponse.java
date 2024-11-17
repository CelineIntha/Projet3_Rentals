package com.rentals.responses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RentalResponse {
    private Integer id;
    private String name;
    private BigDecimal surface;
    private BigDecimal price;
    private String picture;
    private String description;
    private Integer ownerId;
    private String createdAt;
    private String updatedAt;

    public RentalResponse(Integer id, String name, BigDecimal surface, BigDecimal price, String picture, String description, Integer ownerId, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.surface = surface;
        this.price = price;
        this.picture = picture;
        this.description = description;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}