package br.com.academico.api;

import br.com.academico.dto.Requisicoes.*;
import br.com.academico.dto.Respostas.*;
import br.com.academico.servico.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth") @SecurityRequirements
public class AutenticacaoController {
    private final AlunoServico alunos;
    private final AutenticacaoServico autenticacao;
    public AutenticacaoController(AlunoServico alunos, AutenticacaoServico autenticacao) { this.alunos = alunos; this.autenticacao = autenticacao; }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public AlunoDto cadastrar(@Valid @RequestBody Cadastro dados) { return alunos.cadastrar(dados); }
    @PostMapping("/login")
    public TokenDto entrar(@Valid @RequestBody Login dados) { return autenticacao.autenticar(dados); }
}
