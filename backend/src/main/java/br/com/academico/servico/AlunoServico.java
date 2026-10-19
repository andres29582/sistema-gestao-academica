package br.com.academico.servico;

import br.com.academico.dominio.*;
import br.com.academico.dto.Requisicoes.*;
import br.com.academico.dto.Respostas.*;
import br.com.academico.erro.RegraException;
import br.com.academico.repositorio.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional(readOnly = true)
public class AlunoServico {
    private final AlunoRepositorio alunos;
    private final CursoRepositorio cursos;
    private final MatriculaRepositorio matriculas;
    private final ContaServico contas;
    public AlunoServico(AlunoRepositorio alunos, CursoRepositorio cursos, MatriculaRepositorio matriculas, ContaServico contas) {
        this.alunos = alunos; this.cursos = cursos; this.matriculas = matriculas; this.contas = contas;
    }
    @Transactional
    public AlunoDto cadastrar(Cadastro dados) {
        var curso = cursoDisponivel(dados.cursoId());
        var usuario = contas.criar(dados.nome(), dados.email(), dados.telefone(), dados.senha(), Papel.ALUNO);
        return AlunoDto.de(alunos.saveAndFlush(new Aluno(usuario, curso)));
    }
    public List<AlunoDto> listar() {
        return alunos.findByUsuarioExcluidoEmIsNullOrderByIdDesc().stream().map(AlunoDto::de).toList();
    }
    public AlunoDto consultar(Long id) { return AlunoDto.de(obter(id)); }
    public List<AlunoDto> solicitacoes() {
        return alunos.findBySituacaoSolicitacaoAndUsuarioExcluidoEmIsNullOrderByCriadoEmDescIdDesc(SituacaoSolicitacao.PENDENTE)
                .stream().map(AlunoDto::de).toList();
    }
    @Transactional
    public AlunoDto atualizar(Long id, DadosPessoais dados) {
        var aluno = bloquear(id); contas.atualizar(aluno.getUsuario(), dados);
        return AlunoDto.de(aluno);
    }
    @Transactional
    public AlunoDto status(Long id, boolean ativo) {
        var aluno = bloquear(id); aluno.getUsuario().setAtivo(ativo); return AlunoDto.de(aluno);
    }
    @Transactional
    public void excluir(Long id) {
        var aluno = bloquear(id);
        if (matriculas.existsByAlunoIdAndSituacao(id, SituacaoMatricula.ATIVA))
            throw RegraException.conflito("MATRICULA_ATIVA", "Cancele a matrícula ativa antes de excluir o aluno.");
        aluno.getUsuario().excluir();
    }
    @Transactional
    public MatriculaDto aprovar(Long id) {
        var aluno = bloquear(id); exigirPendente(aluno);
        if (matriculas.existsByAlunoId(id))
            throw RegraException.conflito("MATRICULA_JA_EXISTE", "O aluno já possui um registro de matrícula.");
        if (!aluno.getCursoSolicitado().isAtivo())
            throw RegraException.conflito("CURSO_INATIVO", "O curso solicitado não está disponível para aprovação.");
        aluno.aprovar();
        // O flush faz falhas de integridade desfazerem também a aprovação na mesma transação.
        return MatriculaDto.de(matriculas.saveAndFlush(new Matricula(aluno, aluno.getCursoSolicitado())));
    }
    @Transactional
    public AlunoDto rejeitar(Long id) {
        var aluno = bloquear(id); exigirPendente(aluno); aluno.rejeitar(); return AlunoDto.de(aluno);
    }
    public Aluno obter(Long id) {
        var aluno = alunos.findById(id).orElseThrow(RegraException::naoEncontrado);
        if (aluno.getUsuario().getExcluidoEm() != null) throw RegraException.naoEncontrado();
        return aluno;
    }
    public Aluno bloquear(Long id) {
        var aluno = alunos.bloquear(id).orElseThrow(RegraException::naoEncontrado);
        if (aluno.getUsuario().getExcluidoEm() != null) throw RegraException.naoEncontrado();
        return aluno;
    }
    public Curso cursoDisponivel(Long id) {
        var curso = cursos.findById(id).orElseThrow(() -> RegraException.invalido("CURSO_INVALIDO", "Curso inexistente."));
        if (!curso.isAtivo()) throw RegraException.invalido("CURSO_INATIVO", "Selecione um curso ativo.");
        return curso;
    }
    private void exigirPendente(Aluno aluno) {
        if (aluno.getSituacaoSolicitacao() != SituacaoSolicitacao.PENDENTE)
            throw RegraException.conflito("SOLICITACAO_NAO_PENDENTE", "Somente solicitações pendentes podem ser analisadas.");
    }
}
