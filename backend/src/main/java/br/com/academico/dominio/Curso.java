package br.com.academico.dominio;

import jakarta.persistence.*;

@Entity @Table(name = "curso")
public class Curso {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String nome;
    @Column(nullable = false, length = 500)
    private String descricao;
    @Column(nullable = false)
    private boolean ativo;

    protected Curso() { }
    public Curso(String nome, String descricao, boolean ativo) {
        this.nome = nome; this.descricao = descricao; this.ativo = ativo;
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
