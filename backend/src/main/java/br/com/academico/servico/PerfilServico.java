package br.com.academico.servico;

import br.com.academico.dominio.*;
import br.com.academico.dto.Requisicoes.DadosPessoais;
import br.com.academico.dto.Respostas.*;
import br.com.academico.erro.RegraException;
import br.com.academico.repositorio.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional(readOnly = true)
public class PerfilServico {
    private final UsuarioRepositorio usuarios;
    private final AlunoRepositorio alunos;
    private final MatriculaRepositorio matriculas;
    private final AlunoServico alunoServico;
    private final ContaServico contas;
    public PerfilServico(UsuarioRepositorio usuarios, AlunoRepositorio alunos, MatriculaRepositorio matriculas,
            AlunoServico alunoServico, ContaServico contas) {
        this.usuarios = usuarios; this.alunos = alunos; this.matriculas = matriculas; this.alunoServico = alunoServico; this.contas = contas;
    }
    public PerfilDto consultar(Long usuarioId) { return PerfilDto.de(obterUsuario(usuarioId)); }
    @Transactional
    public PerfilDto atualizar(Long usuarioId, DadosPessoais dados) {
        // Descobrir o ID não materializa Usuario antes de aguardar a transação concorrente.
        var alunoId = alunos.buscarIdPorUsuario(usuarioId);
        if (alunoId.isPresent()) {
            var aluno = alunoServico.bloquear(alunoId.get());
            var usuario = aluno.getUsuario();
            if (!usuario.habilitado()) throw RegraException.proibido();
            if (aluno.getSituacaoSolicitacao() != SituacaoSolicitacao.APROVADO
                    || !matriculas.existsByAlunoIdAndSituacao(aluno.getId(), SituacaoMatricula.ATIVA)) throw RegraException.proibido();
            contas.atualizar(usuario, dados); return PerfilDto.de(usuario);
        }
        var usuario = obterUsuario(usuarioId);
        contas.atualizar(usuario, dados); return PerfilDto.de(usuario);
    }
    public AlunoDto solicitacao(Long usuarioId) { return AlunoDto.de(porUsuario(usuarioId)); }
    public MatriculaDto matricula(Long usuarioId) {
        return MatriculaDto.de(matriculas.findByAlunoId(porUsuario(usuarioId).getId()).orElseThrow(RegraException::naoEncontrado));
    }
    private Aluno porUsuario(Long usuarioId) {
        if (obterUsuario(usuarioId).getPapel() != Papel.ALUNO) throw RegraException.proibido();
        return alunos.findByUsuarioId(usuarioId).orElseThrow(RegraException::naoEncontrado);
    }
    private Usuario obterUsuario(Long id) {
        var u = usuarios.findById(id).orElseThrow(RegraException::naoEncontrado);
        if (!u.habilitado()) throw RegraException.proibido();
        return u;
    }
}
