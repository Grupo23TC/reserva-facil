package br.com.fiap.hackathon.reservafacil.repository;

import br.com.fiap.hackathon.reservafacil.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    @Query(value = """
        SELECT u FROM Usuario u JOIN FETCH u.roles WHERE u.cns = :cns
    """)
    Optional<Usuario> findByCns(String cns);
}
