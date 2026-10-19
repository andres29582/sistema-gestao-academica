package br.com.academico.dominio;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "aluno")
public class Aluno {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_solicitado_id", nullable = false)
    private Curso cursoSolicitado;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private SituacaoSolicitacao situacaoSolicitacao = SituacaoSolicitacao.PENDENTE;
    @Column(nullable = false, updatable = false)
    private Instant criadoEm;
    @Column(nullable = false)
    private Instant atualizadoEm;

    protected Aluno() { }
    public Aluno(Usuario usuario, Curso cursoSolicitado) {
        this.usuario = usuario; this.cursoSolicitado = cursoSolicitado;
    }
    @PrePersist
    void criar() { criadoEm = atualizadoEm = Instant.now(); }
    @PreUpdate
    void atualizar() { atualizadoEm = Instant.now(); }
    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Curso getCursoSolicitado() { return cursoSolicitado; }
    public SituacaoSolicitacao getSituacaoSolicitacao() { return situacaoSolicitacao; }
    public Instant getCriadoEm() { return criadoEm; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
    public void aprovar() { situacaoSolicitacao = SituacaoSolicitacao.APROVADO; }
    public void rejeitar() { situacaoSolicitacao = SituacaoSolicitacao.REJEITADO; }
}
