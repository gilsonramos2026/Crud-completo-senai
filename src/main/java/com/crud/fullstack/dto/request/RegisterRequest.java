package com.crud.fullstack.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 100)
        String username,

        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 8, message = "Senha deve ter ao menos 8 caracteres")
        String password
) {}