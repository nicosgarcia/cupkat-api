package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {
    @Id
    private String id;                 // ex.: "chocolate"
    private String nome;
    private String titulo;
    @Column(precision = 10, scale = 2) private BigDecimal preco;
    @Column(precision = 10, scale = 2) private BigDecimal precoAntigo;
    private Double avaliacao;
    @Column(length = 1000) private String descricao;
    private boolean maisVendido;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public BigDecimal getPreco() {
		return preco;
	}
	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}
	public BigDecimal getPrecoAntigo() {
		return precoAntigo;
	}
	public void setPrecoAntigo(BigDecimal precoAntigo) {
		this.precoAntigo = precoAntigo;
	}
	public Double getAvaliacao() {
		return avaliacao;
	}
	public void setAvaliacao(Double avaliacao) {
		this.avaliacao = avaliacao;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public boolean isMaisVendido() {
		return maisVendido;
	}
	public void setMaisVendido(boolean maisVendido) {
		this.maisVendido = maisVendido;
	}
}