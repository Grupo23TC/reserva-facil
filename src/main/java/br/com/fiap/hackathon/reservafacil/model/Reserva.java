package br.com.fiap.hackathon.reservafacil.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDateTime dataReserva;

    @ManyToOne
    private Beneficiario beneficiario;

    @ManyToOne
    private Prestador prestador;

    @ManyToOne
    private Medicamento medicamento;

}
