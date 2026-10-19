package br.com.academico.erro;

import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class TratadorErros {
    public static ProblemDetail problema(HttpStatus status, String codigo, String detalhe) {
        var p = ProblemDetail.forStatusAndDetail(status, detalhe);
        p.setTitle(switch (status) {
            case BAD_REQUEST -> "Dados inválidos";
            case UNAUTHORIZED -> "Autenticação necessária";
            case FORBIDDEN -> "Acesso negado";
            case NOT_FOUND -> "Recurso não encontrado";
            case CONFLICT -> "Conflito de estado";
            default -> "Erro na solicitação";
        });
        p.setType(URI.create("urn:academico:erro:" + codigo.toLowerCase(java.util.Locale.ROOT)));
        p.setProperty("codigo", codigo); p.setProperty("ocorridoEm", Instant.now());
        return p;
    }
    @ExceptionHandler(RegraException.class)
    ProblemDetail regra(RegraException e) { return problema(e.getStatus(), e.getCodigo(), e.getMessage()); }
    @ExceptionHandler(BindException.class)
    ProblemDetail validacao(BindException e) {
        var p = problema(HttpStatus.BAD_REQUEST, "DADOS_INVALIDOS", "Revise os campos informados.");
        List<String> campos = e.getBindingResult().getFieldErrors().stream().map(f -> f.getField() + ": " + f.getDefaultMessage()).toList();
        p.setProperty("campos", campos); return p;
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class, ConstraintViolationException.class})
    ProblemDetail formato(Exception e) { return problema(HttpStatus.BAD_REQUEST, "REQUISICAO_INVALIDA", "Formato da solicitação inválido ou campo não permitido."); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail integridade(DataIntegrityViolationException e) { return problema(HttpStatus.CONFLICT, "CONFLITO_INTEGRIDADE", "A operação conflita com um registro existente."); }
    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail proibido(AccessDeniedException e) { return problema(HttpStatus.FORBIDDEN, "ACESSO_NEGADO", "Você não tem permissão para esta operação."); }
}
