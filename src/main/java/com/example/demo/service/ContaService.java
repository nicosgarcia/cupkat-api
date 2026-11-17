package com.example.demo.service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.exception.NegocioException;
import com.example.demo.model.Pedido;
import com.example.demo.model.StatusPedido;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EnderecoRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.TokenRecuperacaoRepository;
import com.example.demo.repository.UsuarioRepository;

@Service
public class ContaService {

    private static final Set<StatusPedido> FINALIZADOS =
            EnumSet.of(StatusPedido.ENTREGUE, StatusPedido.CANCELADO);

    private final UsuarioRepository usuarios;
    private final PedidoRepository pedidos;
    private final EnderecoRepository enderecos;
    private final TokenRecuperacaoRepository tokens;
    private final PasswordEncoder encoder;

    public ContaService(UsuarioRepository usuarios, PedidoRepository pedidos,
                        EnderecoRepository enderecos, TokenRecuperacaoRepository tokens,
                        PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.pedidos = pedidos;
        this.enderecos = enderecos;
        this.tokens = tokens;
        this.encoder = encoder;
    }

    @Transactional
    public void excluir(String email, String senha) {
        Usuario usuario = usuarios.findByEmail(email)
                .orElseThrow(() -> new NegocioException(HttpStatus.UNAUTHORIZED, "Sessão inválida."));

        if (!encoder.matches(senha, usuario.getSenhaHash())) {
            throw new NegocioException(HttpStatus.BAD_REQUEST, "Senha incorreta.");
        }

        List<Pedido> doUsuario = pedidos.findByUsuarioEmailOrderByIdDesc(email);
        boolean emAndamento = doUsuario.stream().anyMatch(p -> !FINALIZADOS.contains(p.getStatus()));
        if (emAndamento) {
            throw new NegocioException(HttpStatus.CONFLICT,
                    "Você tem pedidos em andamento. Cancele ou aguarde a entrega antes de excluir a conta.");
        }

        pedidos.deleteAll(doUsuario);
        enderecos.deleteAll(enderecos.findByUsuarioEmailOrderByIdDesc(email));
        tokens.deleteByUsuarioId(usuario.getId());
        usuarios.delete(usuario);
    }
}