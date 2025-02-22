package br.com.fiap.hackathon.reservafacil.exception.handler;

import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.role.RoleNaoEncontradaException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioNaoIguaisException;
import br.com.fiap.hackathon.reservafacil.exception.validacao.ErroCustomizado;
import br.com.fiap.hackathon.reservafacil.exception.validacao.ValidacaoErro;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BeneficiarioCadastradoException.class)
    public ResponseEntity<ErroCustomizado> handleBeneficiarioCadastradoException(BeneficiarioCadastradoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        ErroCustomizado erro = new ErroCustomizado(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(BeneficiarioNaoEncontradoException.class)
    public ResponseEntity<ErroCustomizado> handleBeneficiarioNaoEncontradoException(BeneficiarioNaoEncontradoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErroCustomizado erro = new ErroCustomizado(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(RoleNaoEncontradaException.class)
    public ResponseEntity<ErroCustomizado> handleRoleNaoEncontradaException(RoleNaoEncontradaException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErroCustomizado erro = new ErroCustomizado(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(UsuarioCadastradoException.class)
    public ResponseEntity<ErroCustomizado> handleUsuarioCadastradoException(UsuarioCadastradoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        ErroCustomizado erro = new ErroCustomizado(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErroCustomizado> handleUsuarioNaoEncontradoException(UsuarioNaoEncontradoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErroCustomizado erro = new ErroCustomizado(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(UsuarioNaoIguaisException.class)
    public ResponseEntity<ErroCustomizado> handleUsuarioNaoIguaisException(UsuarioNaoIguaisException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        ErroCustomizado erro = new ErroCustomizado(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroCustomizado> methodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        ValidacaoErro erro = new ValidacaoErro(
                LocalDateTime.now(),
                status.value(),
                "Dado(s) inválido(s)",
                request.getRequestURI()
        );

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            erro.addErros(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErroCustomizado> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErroCustomizado erro = new ErroCustomizado(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }
}
