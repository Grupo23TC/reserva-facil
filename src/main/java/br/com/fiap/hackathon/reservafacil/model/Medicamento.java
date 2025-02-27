package br.com.fiap.hackathon.reservafacil.model;

import br.com.fiap.hackathon.reservafacil.model.enums.TipoMedicamentoEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "medicamento")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "lote")
    private String lote;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Documento> documentos;

    @Column(name = "quantidade")
    private int quantidade;

    @Column(name = "validade")
    private LocalDate validade;

    @Column(name = "tipo_medicamento")
    private TipoMedicamentoEnum tipoMedicamentoEnum;

    @ManyToOne
    @JoinColumn(name="prestadorId")
    private Prestador prestador;

}

