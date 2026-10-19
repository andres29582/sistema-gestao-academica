package br.com.academico.repositorio;

import br.com.academico.dominio.*;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;

public interface MatriculaRepositorio extends JpaRepository<Matricula, Long> {
    @Query("select m.aluno.id from Matricula m where m.id = :id")
    Optional<Long> buscarAlunoId(Long id);
    Optional<Matricula> findByAlunoId(Long alunoId);
    boolean existsByAlunoId(Long alunoId);
    boolean existsByAlunoIdAndSituacao(Long alunoId, SituacaoMatricula situacao);
    List<Matricula> findByAlunoUsuarioExcluidoEmIsNullOrderByIdDesc();
    List<Matricula> findByCursoIdAndSituacaoAndAlunoUsuarioAtivoTrueAndAlunoUsuarioExcluidoEmIsNullOrderByAlunoUsuarioNomeAsc(Long cursoId, SituacaoMatricula situacao);
    long countBySituacao(SituacaoMatricula situacao);
    @Query("select count(m) from Matricula m where m.situacao = br.com.academico.dominio.SituacaoMatricula.ATIVA and m.aluno.situacaoSolicitacao = br.com.academico.dominio.SituacaoSolicitacao.APROVADO and m.aluno.usuario.ativo = true and m.aluno.usuario.excluidoEm is null")
    long contarAlunosAtivos();
}
