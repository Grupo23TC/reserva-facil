package br.com.fiap.hackathon.reservafacil.model;

import br.com.fiap.hackathon.reservafacil.model.enums.TipoPrestadorEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "prestador")
public class Prestador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="enderecoId")
    private Endereco endereco;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_prestador")
    private TipoPrestadorEnum tipoPrestadorEnum;

    @ToString.Exclude
    @OneToMany(mappedBy = "prestador", cascade = CascadeType.ALL)
    private List<Medicamento> medicamentos;

    @OneToMany(mappedBy = "prestador", cascade = CascadeType.ALL)
    private List<Operador> operadores;
}
