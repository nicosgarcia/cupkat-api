package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaRequest(
    @NotBlank(message = "Link inválido.") String token,

    @NotBlank(message = "Informe a nova senha.")
    @Size(min = 8, max = 72, message = "A senha deve ter de 8 a 72 caracteres.")
    @Pattern(regexp = "\\S*", message = "A senha não pode conter espaços.")
    String novaSenha
) {}