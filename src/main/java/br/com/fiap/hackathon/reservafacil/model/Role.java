package br.com.fiap.hackathon.reservafacil.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String authority;


    @Override
    public String getAuthority() {
        return authority;
    }
}
