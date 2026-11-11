package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record PedidoRequest(
    @NotNull(message = "Informe o endereço de entrega.")
    Long enderecoId,

    @NotEmpty(message = "O carrinho está vazio.")
    @Size(max = 50, message = "Há itens demais no pedido.")
    List<@Valid Item> itens
) {
    public record Item(
        @NotBlank(message = "Informe o produto.")
        String produtoId,

        @NotBlank(message = "Informe a customização.")
        String customizacao,

        @NotNull(message = "Informe a quantidade.")
        @Min(value = 1, message = "A quantidade mínima é 1.")
        @Max(value = 100, message = "A quantidade máxima é 100.")   // ← altere aqui se quiser outro limite
        Integer quantidade
    ) {}
}