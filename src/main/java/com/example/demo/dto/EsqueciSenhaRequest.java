package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EsqueciSenhaRequest(
    @NotBlank(message = "Informe o e-mail.") @Email(message = "E-mail inválido.") String email
) {}