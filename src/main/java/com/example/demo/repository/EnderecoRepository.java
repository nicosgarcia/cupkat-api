package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findByUsuarioEmailOrderByIdDesc(String email);
    Optional<Endereco> findByIdAndUsuarioEmail(Long id, String email);
}