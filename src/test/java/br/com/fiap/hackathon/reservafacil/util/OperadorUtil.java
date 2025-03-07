package br.com.fiap.hackathon.reservafacil.util;

import br.com.fiap.hackathon.reservafacil.model.Operador;
import br.com.fiap.hackathon.reservafacil.model.dto.operador.CadastrarOperadorRequest;

import java.util.UUID;

import static br.com.fiap.hackathon.reservafacil.util.PrestadorUtil.gerarPrestador;
import static br.com.fiap.hackathon.reservafacil.util.UsuarioUtil.*;

public class OperadorUtil {
    public static Operador gerarOperador() {
        Operador operador = new Operador();
        operador.setCns("234567890123456");
        operador.setNome("Operador");
        operador.setCargo("Farmacêutico");
        operador.setPrestador(gerarPrestador());
        operador.setAtivo(true);
        operador.setUsuario(gerarUsuario());

        return operador;
    }

    public static Operador gerarOperador(UUID idPrestador) {
        Operador operador = new Operador();
        operador.setCns("234567890123456");
        operador.setNome("Operador");
        operador.setCargo("Farmacêutico");
        operador.setPrestador(gerarPrestador(idPrestador));
        operador.setAtivo(true);
        operador.setUsuario(gerarUsuario());

        return operador;
    }

    public static Operador gerarOperador(boolean ativo) {
        Operador operador = new Operador();
        operador.setCns("234567890123456");
        operador.setNome("Operador");
        operador.setCargo("Farmacêutico");
        operador.setPrestador(gerarPrestador());
        operador.setAtivo(ativo);
        operador.setUsuario(gerarUsuario());

        return operador;
    }

    public static Operador gerarOperador(String cns, boolean ativo) {
        Operador operador = new Operador();
        operador.setCns(cns);
        operador.setNome("Operador");
        operador.setCargo("Farmacêutico");
        operador.setPrestador(gerarPrestador());
        operador.setAtivo(ativo);
        operador.setUsuario(gerarUsuario());

        return operador;
    }

    public static CadastrarOperadorRequest gerarCadastrarOperadorRequest() {
        return new CadastrarOperadorRequest(
                gerarCadastrarUsuarioRequestRoleOperador(),
                "Jonas",
                "Farmacêutico",
                UUID.randomUUID()
        );
    }

    public static CadastrarOperadorRequest gerarCadastrarOperadorRequest(String cns) {
        return new CadastrarOperadorRequest(
                gerarCadastrarUsuarioRequest(cns, "OPERADOR"),
                "Jonas",
                "Farmacêutico",
                UUID.randomUUID()
        );
    }

    public static CadastrarOperadorRequest gerarCadastrarOperadorRequest(UUID prestadorId) {
        return new CadastrarOperadorRequest(
                gerarCadastrarUsuarioRequestRoleOperador(),
                "Jonas",
                "Farmacêutico",
                prestadorId
        );
    }

    public static CadastrarOperadorRequest gerarCadastrarOperadorRequest(String cns, UUID prestadorId) {
        return new CadastrarOperadorRequest(
                gerarCadastrarUsuarioRequest(cns, "OPERADOR"),
                "Jonas",
                "Farmacêutico",
                prestadorId
        );
    }
}
