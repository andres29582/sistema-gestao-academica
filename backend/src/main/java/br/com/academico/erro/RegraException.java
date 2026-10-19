package br.com.academico.erro;

import org.springframework.http.HttpStatus;

public class RegraException extends RuntimeException {
    private final HttpStatus status;
    private final String codigo;
    public RegraException(HttpStatus status, String codigo, String mensagem) {
        super(mensagem); this.status = status; this.codigo = codigo;
    }
    public HttpStatus getStatus() { return status; }
    public String getCodigo() { return codigo; }
    public static RegraException naoEncontrado() { return new RegraException(HttpStatus.NOT_FOUND, "RECURSO_NAO_ENCONTRADO", "Recurso não encontrado."); }
    public static RegraException conflito(String codigo, String mensagem) { return new RegraException(HttpStatus.CONFLICT, codigo, mensagem); }
    public static RegraException invalido(String codigo, String mensagem) { return new RegraException(HttpStatus.BAD_REQUEST, codigo, mensagem); }
    public static RegraException proibido() { return new RegraException(HttpStatus.FORBIDDEN, "ACESSO_NEGADO", "Você não tem permissão para esta operação."); }
}
