package br.com.academico.api;

import br.com.academico.dto.Requisicoes.*;
import br.com.academico.dto.Respostas.*;
import br.com.academico.seguranca.ContaAutenticada;
import br.com.academico.servico.ProfessorServico;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/professores")
public class ProfessorController {
    private final ProfessorServico professores;
    public ProfessorController(ProfessorServico professores) { this.professores = professores; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ProfessorDto cadastrar(@Valid @RequestBody CriarProfessor dados) { return professores.cadastrar(dados); }
    @GetMapping
    public List<ProfessorDto> listar() { return professores.listar(); }
    @GetMapping("/{id}")
    public ProfessorDto consultar(@PathVariable Long id) { return professores.consultar(id); }
    @PutMapping("/{id}")
    public ProfessorDto atualizar(@PathVariable Long id, @Valid @RequestBody EditarProfessor dados) { return professores.atualizar(id, dados); }
    @PatchMapping("/{id}/status")
    public ProfessorDto status(@PathVariable Long id, @Valid @RequestBody StatusConta dados) { return professores.status(id, dados.ativo()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) { professores.excluir(id); }
    @GetMapping("/me/cursos")
    public List<CursoDto> meusCursos(@AuthenticationPrincipal ContaAutenticada conta) { return professores.meusCursos(conta.usuarioId()); }
    @GetMapping("/me/cursos/{cursoId}/alunos")
    public List<AlunoCursoDto> alunos(@AuthenticationPrincipal ContaAutenticada conta, @PathVariable Long cursoId) {
        return professores.alunosDoCurso(conta.usuarioId(), cursoId);
    }
}
