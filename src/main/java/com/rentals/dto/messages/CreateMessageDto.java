package com.rentals.dto.messages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateMessageDto {

    @NotBlank(message = "Le message est obligatoire")
    private String message;

    @NotNull(message = "L'ID du propriétaire est obligatoire")
    private Integer ownerId;

    @NotNull(message = "L'ID de la location est obligatoire")
    private Integer rentalId;
}