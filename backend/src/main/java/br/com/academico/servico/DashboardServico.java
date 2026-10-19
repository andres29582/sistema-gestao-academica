package br.com.academico.servico;

import br.com.academico.dominio.*;
import br.com.academico.dto.Respostas.*;
import br.com.academico.repositorio.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional(readOnly = true)
public class DashboardServico {
    private final AlunoRepositorio alunos;
    private final ProfessorRepositorio professores;
    private final MatriculaRepositorio matriculas;
    private final CursoRepositorio cursos;
    public DashboardServico(AlunoRepositorio alunos, ProfessorRepositorio professores, MatriculaRepositorio matriculas, CursoRepositorio cursos) {
        this.alunos = alunos; this.professores = professores; this.matriculas = matriculas; this.cursos = cursos;
    }
    public DashboardDto consultar() {
        return new DashboardDto(matriculas.contarAlunosAtivos(), professores.countByUsuarioAtivoTrueAndUsuarioExcluidoEmIsNull(),
                alunos.countBySituacaoSolicitacaoAndUsuarioExcluidoEmIsNull(SituacaoSolicitacao.PENDENTE),
                matriculas.countBySituacao(SituacaoMatricula.ATIVA), cursos.countByAtivoTrue(),
                alunos.findTop5ByUsuarioExcluidoEmIsNullOrderByCriadoEmDescIdDesc().stream().map(AlunoDto::de).toList());
    }
}
