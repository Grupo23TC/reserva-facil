package br.com.fiap.hackathon.reservafacil.exception.handler;

import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoEsgotadoException;
import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoNaoPertencePrestadorException;
import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoRestritoException;
import br.com.fiap.hackathon.reservafacil.exception.prestador.PrestadorNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.reserva.DataReservaInvalidaException;
import br.com.fiap.hackathon.reservafacil.exception.reserva.DataReservaNaoDisponivelException;
import br.com.fiap.hackathon.reservafacil.exception.reserva.ReservaNaoEncontradaException;
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

import java.time.LocalDateTime;

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

    @ExceptionHandler(PrestadorNaoEncontradoException.class)
    public ResponseEntity<ErroCustomizado> handlePrestadorNaoEncontradoException(PrestadorNaoEncontradoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErroCustomizado erro = new ErroCustomizado(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MedicamentoNaoEncontradoException.class)
    public ResponseEntity<ErroCustomizado> handleMedicamentoNaoEncontradoException(MedicamentoNaoEncontradoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErroCustomizado erro = new ErroCustomizado(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(ReservaNaoEncontradaException.class)
    public ResponseEntity<ErroCustomizado> handleReservaNaoEncontradaException(ReservaNaoEncontradaException ex, HttpServletRequest request) {
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

    @ExceptionHandler(DataReservaInvalidaException.class)
    public ResponseEntity<ErroCustomizado> handleDataReservaInvalidaException(DataReservaInvalidaException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErroCustomizado erro = new ErroCustomizado(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(DataReservaNaoDisponivelException.class)
    public ResponseEntity<ErroCustomizado> handleDataReservaNaoDisponivelException(DataReservaNaoDisponivelException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        ErroCustomizado erro = new ErroCustomizado(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MedicamentoNaoPertencePrestadorException.class)
    public ResponseEntity<ErroCustomizado> handleMedicamentoNaoPertencePrestadorException(MedicamentoNaoPertencePrestadorException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        ErroCustomizado erro = new ErroCustomizado(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MedicamentoRestritoException.class)
    public ResponseEntity<ErroCustomizado> handleMedicamentoRestritoException(MedicamentoRestritoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        ErroCustomizado erro = new ErroCustomizado(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MedicamentoEsgotadoException.class)
    public ResponseEntity<ErroCustomizado> handleMedicamentoEsgotadoException(MedicamentoEsgotadoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

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
