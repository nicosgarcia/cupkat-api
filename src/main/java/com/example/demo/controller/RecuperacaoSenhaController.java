package com.example.demo.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.EsqueciSenhaRequest;
import com.example.demo.dto.RedefinirSenhaRequest;
import com.example.demo.service.RecuperacaoSenhaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService service;

    public RecuperacaoSenhaController(RecuperacaoSenhaService service) { this.service = service; }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<Map<String, String>> esqueci(@Valid @RequestBody EsqueciSenhaRequest req) {
        service.solicitar(req.email());
        return ResponseEntity.ok(Map.of("mensagem",
                "Se o e-mail estiver cadastrado, enviamos um link para redefinir a senha."));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Map<String, String>> redefinir(@Valid @RequestBody RedefinirSenhaRequest req) {
        service.redefinir(req.token(), req.novaSenha());
        return ResponseEntity.ok(Map.of("mensagem",
                "Senha alterada com sucesso. Faça login com a nova senha."));
    }
}