package br.com.academico.servico;

import br.com.academico.dominio.*;
import br.com.academico.dto.Requisicoes.DadosPessoais;
import br.com.academico.erro.RegraException;
import br.com.academico.repositorio.UsuarioRepositorio;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ContaServico {
    private final UsuarioRepositorio usuarios;
    private final PasswordEncoder senhas;
    public ContaServico(UsuarioRepositorio usuarios, PasswordEncoder senhas) { this.usuarios = usuarios; this.senhas = senhas; }
    public Usuario criar(String nome, String email, String telefone, String senha, Papel papel) {
        if (usuarios.existsByEmail(email)) throw emailDuplicado();
        return usuarios.save(new Usuario(nome.strip(), email, telefone.strip(), senhas.encode(senha), papel));
    }
    public void atualizar(Usuario usuario, DadosPessoais dados) {
        if (usuarios.existsByEmailAndIdNot(dados.email(), usuario.getId())) throw emailDuplicado();
        usuario.atualizarDados(dados.nome().strip(), dados.email(), dados.telefone().strip());
    }
    private RegraException emailDuplicado() {
        return RegraException.conflito("EMAIL_JA_CADASTRADO", "O e-mail informado já está cadastrado.");
    }
}
