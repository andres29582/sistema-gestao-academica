package br.com.academico.api;

import br.com.academico.dto.Requisicoes.DadosPessoais;
import br.com.academico.dto.Respostas.*;
import br.com.academico.seguranca.ContaAutenticada;
import br.com.academico.servico.PerfilServico;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/me")
public class PerfilController {
    private final PerfilServico perfis;
    public PerfilController(PerfilServico perfis) { this.perfis = perfis; }
    @GetMapping
    public PerfilDto consultar(@AuthenticationPrincipal ContaAutenticada conta) { return perfis.consultar(conta.usuarioId()); }
    @PutMapping
    public PerfilDto atualizar(@AuthenticationPrincipal ContaAutenticada conta, @Valid @RequestBody DadosPessoais dados) {
        return perfis.atualizar(conta.usuarioId(), dados);
    }
    @GetMapping("/solicitacao")
    public AlunoDto solicitacao(@AuthenticationPrincipal ContaAutenticada conta) { return perfis.solicitacao(conta.usuarioId()); }
    @GetMapping("/matricula")
    public MatriculaDto matricula(@AuthenticationPrincipal ContaAutenticada conta) { return perfis.matricula(conta.usuarioId()); }
}
