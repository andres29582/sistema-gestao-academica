package br.com.academico.servico;

import br.com.academico.dominio.*;
import br.com.academico.dto.Respostas.MatriculaDto;
import br.com.academico.erro.RegraException;
import br.com.academico.repositorio.MatriculaRepositorio;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional(readOnly = true)
public class MatriculaServico {
    private final MatriculaRepositorio matriculas;
    private final AlunoServico alunos;
    public MatriculaServico(MatriculaRepositorio matriculas, AlunoServico alunos) { this.matriculas = matriculas; this.alunos = alunos; }
    public List<MatriculaDto> listar() {
        return matriculas.findByAlunoUsuarioExcluidoEmIsNullOrderByIdDesc().stream().map(MatriculaDto::de).toList();
    }
    public MatriculaDto consultar(Long id) { return MatriculaDto.de(obter(id)); }
    @Transactional
    public MatriculaDto atualizar(Long id, Long cursoId) {
        var m = bloquear(id); exigirAtiva(m); m.setCurso(alunos.cursoDisponivel(cursoId)); return MatriculaDto.de(m);
    }
    @Transactional
    public void cancelar(Long id) { var m = bloquear(id); exigirAtiva(m); m.cancelar(); }
    private Matricula obter(Long id) {
        var m = matriculas.findById(id).orElseThrow(RegraException::naoEncontrado);
        if (m.getAluno().getUsuario().getExcluidoEm() != null) throw RegraException.naoEncontrado();
        return m;
    }
    private Matricula bloquear(Long id) {
        // A projeção de ID evita carregar uma matrícula obsoleta antes de aguardar o lock do aluno.
        Long alunoId = matriculas.buscarAlunoId(id).orElseThrow(RegraException::naoEncontrado);
        alunos.bloquear(alunoId);
        return obter(id);
    }
    private void exigirAtiva(Matricula m) {
        if (m.getSituacao() != SituacaoMatricula.ATIVA)
            throw RegraException.conflito("MATRICULA_CANCELADA", "A matrícula já está cancelada.");
    }
}
