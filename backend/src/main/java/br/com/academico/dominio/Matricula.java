package br.com.academico.dominio;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "matricula", uniqueConstraints = @UniqueConstraint(name = "uk_matricula_aluno", columnNames = "aluno_id"))
public class Matricula {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private SituacaoMatricula situacao = SituacaoMatricula.ATIVA;
    @Column(nullable = false, updatable = false)
    private Instant matriculadoEm;
    private Instant canceladoEm;

    protected Matricula() { }
    public Matricula(Aluno aluno, Curso curso) { this.aluno = aluno; this.curso = curso; }
    @PrePersist
    void criar() { matriculadoEm = Instant.now(); }
    public Long getId() { return id; }
    public Aluno getAluno() { return aluno; }
    public Curso getCurso() { return curso; }
    public SituacaoMatricula getSituacao() { return situacao; }
    public Instant getMatriculadoEm() { return matriculadoEm; }
    public Instant getCanceladoEm() { return canceladoEm; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public void cancelar() { situacao = SituacaoMatricula.CANCELADA; canceladoEm = Instant.now(); }
}
