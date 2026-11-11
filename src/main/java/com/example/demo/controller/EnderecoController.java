package com.example.demo.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.EnderecoRequest;
import com.example.demo.dto.EnderecoResponse;
import com.example.demo.model.Endereco;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EnderecoRepository;
import com.example.demo.repository.UsuarioRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/enderecos")
public class EnderecoController {

    private final EnderecoRepository enderecos;
    private final UsuarioRepository usuarios;

    public EnderecoController(EnderecoRepository enderecos, UsuarioRepository usuarios) {
        this.enderecos = enderecos;
        this.usuarios = usuarios;
    }

    @GetMapping
    public List<EnderecoResponse> listar(Authentication auth) {
        return enderecos.findByUsuarioEmailOrderByIdDesc(auth.getName())
                .stream().map(EnderecoResponse::de).toList();
    }

    @PostMapping
    public ResponseEntity<EnderecoResponse> criar(@Valid @RequestBody EnderecoRequest req,
                                                  Authentication auth) {
        Usuario dono = usuarios.findByEmail(auth.getName()).orElse(null);
        if (dono == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Endereco e = new Endereco();
        e.setUsuario(dono);
        aplicar(e, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(EnderecoResponse.de(enderecos.save(e)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponse> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody EnderecoRequest req,
                                                      Authentication auth) {
        return enderecos.findByIdAndUsuarioEmail(id, auth.getName())
                .map(e -> {
                    aplicar(e, req);
                    return ResponseEntity.ok(EnderecoResponse.de(enderecos.save(e)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication auth) {
        return enderecos.findByIdAndUsuarioEmail(id, auth.getName())
                .map(e -> {
                    enderecos.delete(e);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** Copia os dados do pedido para a entidade, já normalizados. */
    private void aplicar(Endereco e, EnderecoRequest r) {
        e.setCep(r.cep().replace("-", ""));
        e.setRua(r.rua().trim());
        e.setNumero(r.numero().trim());
        String comp = r.complemento() == null ? "" : r.complemento().trim();
        e.setComplemento(comp.isEmpty() ? null : comp);
        e.setBairro(r.bairro().trim());
        e.setCidade(r.cidade().trim());
        e.setEstado(r.estado().trim().toUpperCase());
    }
}