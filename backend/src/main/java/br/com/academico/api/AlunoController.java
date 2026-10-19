package br.com.academico.api;

import br.com.academico.dto.Requisicoes.*;
import br.com.academico.dto.Respostas.AlunoDto;
import br.com.academico.servico.AlunoServico;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/alunos")
public class AlunoController {
    private final AlunoServico alunos;
    public AlunoController(AlunoServico alunos) { this.alunos = alunos; }
    @GetMapping
    public List<AlunoDto> listar() { return alunos.listar(); }
    @GetMapping("/{id}")
    public AlunoDto consultar(@PathVariable Long id) { return alunos.consultar(id); }
    @PutMapping("/{id}")
    public AlunoDto atualizar(@PathVariable Long id, @Valid @RequestBody DadosPessoais dados) { return alunos.atualizar(id, dados); }
    @PatchMapping("/{id}/status")
    public AlunoDto status(@PathVariable Long id, @Valid @RequestBody StatusConta dados) { return alunos.status(id, dados.ativo()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) { alunos.excluir(id); }
}
