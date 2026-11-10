package com.example.demo.controller;

import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.*;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.JwtService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody CadastroRequest req) {
        String email = req.email().trim().toLowerCase();

        if (usuarios.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensagem", "Este e-mail já está cadastrado."));
        }

        Usuario u = new Usuario();
        u.setNome(req.nome().trim());
        u.setEmail(email);
        u.setSenhaHash(encoder.encode(req.senha()));
        usuarios.save(u);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(jwt.gerarToken(email), u.getNome(), email));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        Optional<Usuario> achado = usuarios.findByEmail(email);

        if (achado.isEmpty() || !encoder.matches(req.senha(), achado.get().getSenhaHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "E-mail ou senha inválidos."));
        }

        Usuario u = achado.get();
        return ResponseEntity.ok(new AuthResponse(jwt.gerarToken(email), u.getNome(), email));
    }

    /** Rota protegida: serve para testar se o token está funcionando. */
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        Optional<Usuario> achado = usuarios.findByEmail(auth.getName());
        if (achado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario u = achado.get();
        return ResponseEntity.ok(Map.of("nome", u.getNome(), "email", u.getEmail()));
    }
}