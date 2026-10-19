package br.com.academico.api;

import br.com.academico.dto.Respostas.CursoDto;
import br.com.academico.repositorio.CursoRepositorio;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/cursos") @SecurityRequirements
public class CursoController {
    private final CursoRepositorio cursos;
    public CursoController(CursoRepositorio cursos) { this.cursos = cursos; }
    @GetMapping
    public List<CursoDto> listar() { return cursos.findByAtivoTrueOrderByNomeAsc().stream().map(CursoDto::de).toList(); }
}
