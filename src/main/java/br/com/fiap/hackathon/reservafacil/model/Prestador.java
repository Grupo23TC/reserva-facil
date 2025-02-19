package br.com.fiap.hackathon.reservafacil.model;

import br.com.fiap.hackathon.reservafacil.model.enums.TipoPrestadorEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "prestador")
public class Prestador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(nullable = false)
    private String nomeFantasia;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "tipo_prestador")
    private TipoPrestadorEnum tipoPrestadorEnum;
}
