package com.rentals.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateMessageDto {
    @NotNull(message = "The rental ID is required.")
    @JsonProperty("rental_id")
    private Integer rentalId;

    @NotNull(message = "The user ID is required.")
    @JsonProperty("user_id")
    private Integer userId;

    @NotNull(message = "The message is required.")
    @Size(max = 2000, message = "The message cannot exceed 2000 characters.")
    private String message;
}
