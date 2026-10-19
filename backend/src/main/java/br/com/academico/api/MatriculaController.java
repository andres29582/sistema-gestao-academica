package br.com.academico.api;

import br.com.academico.dto.Requisicoes.AlterarMatricula;
import br.com.academico.dto.Respostas.MatriculaDto;
import br.com.academico.servico.MatriculaServico;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/matriculas")
public class MatriculaController {
    private final MatriculaServico matriculas;
    public MatriculaController(MatriculaServico matriculas) { this.matriculas = matriculas; }
    @GetMapping
    public List<MatriculaDto> listar() { return matriculas.listar(); }
    @GetMapping("/{id}")
    public MatriculaDto consultar(@PathVariable Long id) { return matriculas.consultar(id); }
    @PutMapping("/{id}")
    public MatriculaDto atualizar(@PathVariable Long id, @Valid @RequestBody AlterarMatricula dados) { return matriculas.atualizar(id, dados.cursoId()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable Long id) { matriculas.cancelar(id); }
}
