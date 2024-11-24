package com.rentals.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterUserDto {

    @Email(message = "L'adresse email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

}
