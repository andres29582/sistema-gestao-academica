package br.com.academico.repositorio;

import br.com.academico.dominio.Curso;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepositorio extends JpaRepository<Curso, Long> {
    List<Curso> findByAtivoTrueOrderByNomeAsc();
    long countByAtivoTrue();
}
