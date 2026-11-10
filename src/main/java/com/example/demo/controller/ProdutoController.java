package com.example.demo.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.Produto;
import com.example.demo.repository.ProdutoRepository;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoRepository repo;

    public ProdutoController(ProdutoRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Produto> listar() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscar(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
}