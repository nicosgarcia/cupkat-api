package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EnderecoEntrega {

    @Column(name = "entrega_cep", nullable = false, length = 8)
    private String cep;
    @Column(name = "entrega_rua", nullable = false, length = 160)
    private String rua;
    @Column(name = "entrega_numero", nullable = false, length = 20)
    private String numero;
    @Column(name = "entrega_complemento", length = 80)
    private String complemento;
    @Column(name = "entrega_bairro", nullable = false, length = 100)
    private String bairro;
    @Column(name = "entrega_cidade", nullable = false, length = 100)
    private String cidade;
    @Column(name = "entrega_estado", nullable = false, length = 2)
    private String estado;

    public static EnderecoEntrega de(Endereco e) {
        EnderecoEntrega x = new EnderecoEntrega();
        x.cep = e.getCep();
        x.rua = e.getRua();
        x.numero = e.getNumero();
        x.complemento = e.getComplemento();
        x.bairro = e.getBairro();
        x.cidade = e.getCidade();
        x.estado = e.getEstado();
        return x;
    }

    public String getCep() { return cep; }
    public String getRua() { return rua; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
}