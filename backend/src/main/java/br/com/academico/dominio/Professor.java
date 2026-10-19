package br.com.academico.dominio;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity @Table(name = "professor")
public class Professor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
    @ManyToMany
    @JoinTable(name = "professor_curso", joinColumns = @JoinColumn(name = "professor_id"),
            inverseJoinColumns = @JoinColumn(name = "curso_id"))
    private Set<Curso> cursos = new LinkedHashSet<>();
    @Column(nullable = false, updatable = false)
    private Instant criadoEm;

    protected Professor() { }
    public Professor(Usuario usuario, Set<Curso> cursos) {
        this.usuario = usuario; this.cursos.addAll(cursos);
    }
    @PrePersist
    void criar() { criadoEm = Instant.now(); }
    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Set<Curso> getCursos() { return cursos; }
    public Instant getCriadoEm() { return criadoEm; }
    public void substituirCursos(Set<Curso> cursos) { this.cursos.clear(); this.cursos.addAll(cursos); }
}
