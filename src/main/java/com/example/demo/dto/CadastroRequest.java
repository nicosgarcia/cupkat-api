package com.example.demo.dto;

import jakarta.validation.constraints.*;

public record CadastroRequest(
    @NotBlank(message = "Informe o nome.")
    @Size(max = 120, message = "O nome é muito longo.")
    String nome,

    @NotBlank(message = "Informe o e-mail.")
    @Email(message = "E-mail inválido.")
    String email,

    @NotBlank(message = "Informe a senha.")
    @Size(min = 8, max = 72, message = "A senha deve ter de 8 a 72 caracteres.")
    @Pattern(regexp = "\\S*", message = "A senha não pode conter espaços.")
    String senha
) {}