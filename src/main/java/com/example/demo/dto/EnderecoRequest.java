package com.example.demo.dto;

import jakarta.validation.constraints.*;

public record EnderecoRequest(
    @NotBlank(message = "Informe o CEP.")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "Informe um CEP com 8 números.")
    String cep,

    @NotBlank(message = "Informe a rua ou avenida.")
    @Size(max = 160, message = "A rua é muito longa.")
    String rua,

    @NotBlank(message = "Informe o número.")
    @Size(max = 20, message = "O número é muito longo.")
    String numero,

    @Size(max = 80, message = "O complemento é muito longo.")
    String complemento,

    @NotBlank(message = "Informe o bairro.")
    @Size(max = 100, message = "O bairro é muito longo.")
    String bairro,

    @NotBlank(message = "Informe a cidade.")
    @Size(max = 100, message = "A cidade é muito longa.")
    String cidade,

    @NotBlank(message = "Informe o estado.")
    @Pattern(regexp = "(?i)AC|AL|AP|AM|BA|CE|DF|ES|GO|MA|MT|MS|MG|PA|PB|PR|PE|PI|RJ|RN|RS|RO|RR|SC|SP|SE|TO",
             message = "Informe a sigla do estado (ex.: SP).")
    String estado
) {}