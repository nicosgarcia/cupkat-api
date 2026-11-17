package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.exception.NegocioException;
import com.example.demo.model.TokenRecuperacao;
import com.example.demo.model.Usuario;
import com.example.demo.repository.TokenRecuperacaoRepository;
import com.example.demo.repository.UsuarioRepository;

@Service
public class RecuperacaoSenhaService {

    private static final Duration VALIDADE = Duration.ofMinutes(30);
    private static final String LINK_INVALIDO =
            "Link inválido ou expirado. Peça uma nova recuperação de senha.";

    private final SecureRandom random = new SecureRandom();
    private final UsuarioRepository usuarios;
    private final TokenRecuperacaoRepository tokens;
    private final PasswordEncoder encoder;
    private final EmailService email;
    private final String frontendUrl;

    public RecuperacaoSenhaService(UsuarioRepository usuarios, TokenRecuperacaoRepository tokens,
                                   PasswordEncoder encoder, EmailService email,
                                   @Value("${app.frontend-url}") String frontendUrl) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.encoder = encoder;
        this.email = email;
        this.frontendUrl = frontendUrl;
    }

    @Transactional
    public void solicitar(String emailInformado) {
        usuarios.findByEmail(emailInformado.trim().toLowerCase()).ifPresent(usuario -> {
            tokens.deleteByUsuarioId(usuario.getId());

            byte[] bytes = new byte[32];
            random.nextBytes(bytes);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

            TokenRecuperacao t = new TokenRecuperacao();
            t.setUsuario(usuario);
            t.setTokenHash(hash(token));
            t.setExpiraEm(LocalDateTime.now().plus(VALIDADE));
            tokens.save(t);

            email.enviarRecuperacaoSenha(usuario.getEmail(), usuario.getNome(),
                    frontendUrl + "/redefinir-senha?token=" + token);
        });
    }

    @Transactional
    public void redefinir(String token, String novaSenha) {
        TokenRecuperacao t = tokens.findByTokenHash(hash(token.trim()))
                .orElseThrow(() -> new NegocioException(HttpStatus.BAD_REQUEST, LINK_INVALIDO));

        if (t.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw new NegocioException(HttpStatus.BAD_REQUEST, LINK_INVALIDO);
        }

        Usuario usuario = t.getUsuario();
        usuario.setSenhaHash(encoder.encode(novaSenha));
        usuarios.save(usuario);
        tokens.deleteByUsuarioId(usuario.getId());
    }

    private static String hash(String token) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}