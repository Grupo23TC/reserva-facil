package br.com.fiap.hackathon.reservafacil.model;

import br.com.fiap.hackathon.reservafacil.model.enums.FaixaEtariaEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "beneficiario")
public class Beneficiario {

    @Id
    private String cns;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String telefone;

    @Column(name = "faixa_etaria")
    private FaixaEtariaEnum faixaEtariaEnum;

    @Column(name = "endereco")
    private String endereco;

    //Outros Enums
}
