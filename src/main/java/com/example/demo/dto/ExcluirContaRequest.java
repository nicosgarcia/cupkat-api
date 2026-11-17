package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record ExcluirContaRequest(
    @NotBlank(message = "Informe sua senha para confirmar.") String senha
) {}