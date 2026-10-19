package br.com.academico.api;

import br.com.academico.dto.Respostas.*;
import br.com.academico.servico.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/admin")
public class AdministracaoController {
    private final AlunoServico alunos;
    private final DashboardServico dashboard;
    public AdministracaoController(AlunoServico alunos, DashboardServico dashboard) { this.alunos = alunos; this.dashboard = dashboard; }
    @GetMapping("/solicitacoes")
    public List<AlunoDto> solicitacoes() { return alunos.solicitacoes(); }
    @GetMapping("/solicitacoes/{alunoId}")
    public AlunoDto solicitacao(@PathVariable Long alunoId) { return alunos.consultar(alunoId); }
    @PostMapping("/solicitacoes/{alunoId}/aprovar") @ResponseStatus(HttpStatus.CREATED)
    public MatriculaDto aprovar(@PathVariable Long alunoId) { return alunos.aprovar(alunoId); }
    @PostMapping("/solicitacoes/{alunoId}/rejeitar")
    public AlunoDto rejeitar(@PathVariable Long alunoId) { return alunos.rejeitar(alunoId); }
    @GetMapping("/dashboard")
    public DashboardDto dashboard() { return dashboard.consultar(); }
}
