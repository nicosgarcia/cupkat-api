package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.ExcluirContaRequest;
import com.example.demo.service.ContaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/conta")
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) { this.service = service; }

    /** POST (e não DELETE) porque alguns clientes e proxies ignoram o corpo de um DELETE. */
    @PostMapping("/excluir")
    public ResponseEntity<Void> excluir(@Valid @RequestBody ExcluirContaRequest req,
                                        Authentication auth) {
        service.excluir(auth.getName(), req.senha());
        return ResponseEntity.noContent().build();
    }
}