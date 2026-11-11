package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.example.demo.model.EnderecoEntrega;
import com.example.demo.model.Pedido;

public record PedidoResponse(
    Long id, String codigo, String status, BigDecimal total,
    LocalDateTime criadoEm, Entrega entrega, List<Item> itens
) {
    public record Entrega(String cep, String rua, String numero, String complemento,
                          String bairro, String cidade, String estado) {}

    public record Item(String produtoId, String produtoNome, String customizacao,
                       int quantidade, BigDecimal precoUnitario, BigDecimal subtotal) {}

    public static PedidoResponse de(Pedido p) {
        EnderecoEntrega e = p.getEntrega();
        return new PedidoResponse(
            p.getId(), p.getCodigo(), p.getStatus().name(), p.getTotal(), p.getCriadoEm(),
            new Entrega(e.getCep(), e.getRua(), e.getNumero(), e.getComplemento(),
                        e.getBairro(), e.getCidade(), e.getEstado()),
            p.getItens().stream()
                .map(i -> new Item(i.getProduto().getId(), i.getProduto().getNome(),
                        i.getCustomizacao(), i.getQuantidade(),
                        i.getPrecoUnitario(), i.getSubtotal()))
                .toList());
    }
}