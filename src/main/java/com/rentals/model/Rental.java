package com.rentals.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "RENTALS")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal surface;

    @Column(nullable = false)
    private BigDecimal price;

    private String picture;

    @Column(length = 2000)
    private String description;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}