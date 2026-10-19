package br.com.academico.repositorio;

import br.com.academico.dominio.Professor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepositorio extends JpaRepository<Professor, Long> {
    Optional<Professor> findByUsuarioId(Long usuarioId);
    List<Professor> findByUsuarioExcluidoEmIsNullOrderByIdDesc();
    long countByUsuarioAtivoTrueAndUsuarioExcluidoEmIsNull();
}
