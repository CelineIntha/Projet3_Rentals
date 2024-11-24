package com.rentals.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateMessageDto {
    @NotNull(message = "L'ID de la location est obligatoire.")
    @JsonProperty("rental_id")
    private Integer rentalId;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire.")
    @JsonProperty("user_id")
    private Integer userId;

    @NotNull(message = "Le message est obligatoire.")
    @Size(max = 2000, message = "Le message ne peut pas dépasser 2000 caractères.")
    private String message;
}

