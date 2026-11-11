package com.example.demo.dto;

import com.example.demo.model.Endereco;

public record EnderecoResponse(
    Long id, String cep, String rua, String numero,
    String complemento, String bairro, String cidade, String estado
) {
    public static EnderecoResponse de(Endereco e) {
        return new EnderecoResponse(e.getId(), e.getCep(), e.getRua(), e.getNumero(),
                e.getComplemento(), e.getBairro(), e.getCidade(), e.getEstado());
    }
}