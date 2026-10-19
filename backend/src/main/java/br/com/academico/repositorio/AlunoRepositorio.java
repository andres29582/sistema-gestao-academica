package br.com.academico.repositorio;

import br.com.academico.dominio.*;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;

public interface AlunoRepositorio extends JpaRepository<Aluno, Long> {
    @Query("select a.id from Aluno a where a.usuario.id = :usuarioId")
    Optional<Long> buscarIdPorUsuario(Long usuarioId);
    Optional<Aluno> findByUsuarioId(Long usuarioId);
    List<Aluno> findByUsuarioExcluidoEmIsNullOrderByIdDesc();
    List<Aluno> findBySituacaoSolicitacaoAndUsuarioExcluidoEmIsNullOrderByCriadoEmDescIdDesc(SituacaoSolicitacao situacao);
    List<Aluno> findTop5ByUsuarioExcluidoEmIsNullOrderByCriadoEmDescIdDesc();
    long countBySituacaoSolicitacaoAndUsuarioExcluidoEmIsNull(SituacaoSolicitacao situacao);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Aluno a where a.id = :id")
    Optional<Aluno> bloquear(Long id);
}
