package br.com.fiap.hackathon.reservafacil.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "operador")
public class Operador {
    @Id
    private String cns;
    private String nome;
    private String cargo;
    @OneToOne(cascade = CascadeType.ALL)
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "prestador_id")
    private Prestador prestador;
    private Boolean ativo;
}
