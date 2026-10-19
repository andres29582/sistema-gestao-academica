package br.com.academico.dominio;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@org.hibernate.annotations.DynamicUpdate
@Table(name = "usuario", uniqueConstraints = @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"))
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String nome;
    @Column(nullable = false, length = 254)
    private String email;
    @Column(nullable = false, length = 30)
    private String telefone;
    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Papel papel;
    @Column(nullable = false)
    private boolean ativo = true;
    private Instant excluidoEm;
    @Column(nullable = false, updatable = false)
    private Instant criadoEm;
    @Column(nullable = false)
    private Instant atualizadoEm;

    protected Usuario() { }

    public Usuario(String nome, String email, String telefone, String senhaHash, Papel papel) {
        this.nome = nome; this.email = email; this.telefone = telefone;
        this.senhaHash = senhaHash; this.papel = papel;
    }

    @PrePersist
    void criar() { criadoEm = atualizadoEm = Instant.now(); }
    @PreUpdate
    void atualizar() { atualizadoEm = Instant.now(); }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getSenhaHash() { return senhaHash; }
    public Papel getPapel() { return papel; }
    public boolean isAtivo() { return ativo; }
    public Instant getExcluidoEm() { return excluidoEm; }
    public Instant getCriadoEm() { return criadoEm; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
    public boolean habilitado() { return ativo && excluidoEm == null; }
    public void atualizarDados(String nome, String email, String telefone) {
        this.nome = nome; this.email = email; this.telefone = telefone;
    }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public void excluir() { excluidoEm = Instant.now(); ativo = false; }
}
