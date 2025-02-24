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

    private String nome;

    private TipoMedicamentoEnum tipo;

    private Integer quantidade;

    private LocalDate validade;

    private String lote;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Documento> documentos;

    @ManyToOne
    @JoinColumn(name = "prestador_id")
    private Prestador prestador;
}

