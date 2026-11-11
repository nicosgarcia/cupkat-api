package com.example.demo.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.PedidoRequest;
import com.example.demo.dto.PedidoResponse;
import com.example.demo.service.PedidoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(auth.getName(), req));
    }

    @GetMapping
    public List<PedidoResponse> listar(Authentication auth) {
        return service.listar(auth.getName());
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id, Authentication auth) {
        return service.buscar(auth.getName(), id);
    }

    @PatchMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id, Authentication auth) {
        return service.cancelar(auth.getName(), id);
    }
}