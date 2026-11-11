package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.PedidoRequest;
import com.example.demo.dto.PedidoResponse;
import com.example.demo.exception.NegocioException;
import com.example.demo.model.*;
import com.example.demo.repository.*;

@Service
public class PedidoService {

    /** Opções do campo "Customização" do front-end. Altere aqui se mudar a regra. */
    private static final Set<String> CUSTOMIZACOES = Set.of(
        "Sem customização", "Mensagem no topo", "Topper personalizado", "Embalagem para presente");

    private final PedidoRepository pedidos;
    private final ProdutoRepository produtos;
    private final EnderecoRepository enderecos;
    private final UsuarioRepository usuarios;

    public PedidoService(PedidoRepository pedidos, ProdutoRepository produtos,
                         EnderecoRepository enderecos, UsuarioRepository usuarios) {
        this.pedidos = pedidos;
        this.produtos = produtos;
        this.enderecos = enderecos;
        this.usuarios = usuarios;
    }

    @Transactional
    public PedidoResponse criar(String email, PedidoRequest req) {
        Usuario usuario = usuarios.findByEmail(email)
                .orElseThrow(() -> new NegocioException(HttpStatus.UNAUTHORIZED, "Sessão inválida."));

        Endereco endereco = enderecos.findByIdAndUsuarioEmail(req.enderecoId(), email)
                .orElseThrow(() -> new NegocioException(HttpStatus.BAD_REQUEST, "Endereço de entrega inválido."));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEntrega(EnderecoEntrega.de(endereco));
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        BigDecimal total = BigDecimal.ZERO;
        for (PedidoRequest.Item item : req.itens()) {
            String custom = item.customizacao().trim();
            if (!CUSTOMIZACOES.contains(custom)) {
                throw new NegocioException(HttpStatus.BAD_REQUEST, "Customização inválida: " + custom);
            }
            Produto produto = produtos.findById(item.produtoId())
                    .orElseThrow(() -> new NegocioException(HttpStatus.BAD_REQUEST,
                            "Produto inexistente: " + item.produtoId()));

            BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(item.quantidade()));

            ItemPedido ip = new ItemPedido();
            ip.setPedido(pedido);
            ip.setProduto(produto);
            ip.setCustomizacao(custom);
            ip.setQuantidade(item.quantidade());
            ip.setPrecoUnitario(produto.getPreco());   // preço vem do BANCO, nunca do cliente
            ip.setSubtotal(subtotal);
            pedido.getItens().add(ip);

            total = total.add(subtotal);
        }
        pedido.setTotal(total);

        return PedidoResponse.de(pedidos.save(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(String email) {
        return pedidos.findByUsuarioEmailOrderByIdDesc(email).stream().map(PedidoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscar(String email, Long id) {
        return PedidoResponse.de(achar(email, id));
    }

    @Transactional
    public PedidoResponse cancelar(String email, Long id) {
        Pedido pedido = achar(email, id);
        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new NegocioException(HttpStatus.CONFLICT,
                    "Só é possível cancelar pedidos que ainda não foram pagos.");
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        return PedidoResponse.de(pedido);
    }

    private Pedido achar(String email, Long id) {
        return pedidos.findByIdAndUsuarioEmail(id, email)
                .orElseThrow(() -> new NegocioException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
    }
}