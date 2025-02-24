package br.com.fiap.hackathon.reservafacil.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "documento")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nome;

    public Documento(String nome) {
        this.nome = nome;
    }

    public Documento() {

    }
}
