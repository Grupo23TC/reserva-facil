package br.com.fiap.hackathon.reservafacil.model;

import br.com.fiap.hackathon.reservafacil.model.enums.FaixaEtariaEnum;
import br.com.fiap.hackathon.reservafacil.model.enums.GeneroEnum;
import br.com.fiap.hackathon.reservafacil.model.enums.TipoMedicamentoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "beneficiario")
@AllArgsConstructor
@NoArgsConstructor
public class Beneficiario {

    @Id
    private String cns;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String telefone;

    @Column(name = "faixa_etaria", nullable = false)
    @Enumerated(EnumType.STRING)
    private FaixaEtariaEnum faixaEtariaEnum;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="enderecoId")
    private Endereco endereco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GeneroEnum genero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMedicamentoEnum tipoMedicamento;

    @Column(nullable = false)
    private Boolean ativo;

    @OneToOne(cascade = CascadeType.ALL)
    private Usuario usuario;
}
