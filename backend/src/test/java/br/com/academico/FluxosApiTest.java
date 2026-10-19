package br.com.academico;

import br.com.academico.dominio.*;
import br.com.academico.dto.Requisicoes.Cadastro;
import br.com.academico.erro.RegraException;
import br.com.academico.repositorio.*;
import br.com.academico.servico.AlunoServico;
import com.fasterxml.jackson.databind.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc(print = org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.NONE) @ActiveProfiles("test")
class FluxosApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate db;
    @Autowired UsuarioRepositorio usuarios;
    @Autowired CursoRepositorio cursos;
    @Autowired ProfessorRepositorio professores;
    @Autowired AlunoServico alunos;
    Long curso1, curso2, inativo, professorId;
    String admin, professor;

    @BeforeEach void preparar() throws Exception {
        for (var tabela : List.of("professor_curso", "matricula", "aluno", "professor", "usuario", "curso")) db.update("delete from " + tabela);
        var c1 = cursos.save(new Curso("Programação", "Curso A", true)); curso1 = c1.getId();
        var c2 = cursos.save(new Curso("Matemática", "Curso B", true)); curso2 = c2.getId();
        inativo = cursos.save(new Curso("Antigo", "Indisponível", false)).getId();
        // Só a fixture usa custo 4; cadastro e carga demonstrativa reais continuam com BCrypt 12.
        String hash = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(4).encode("Teste@12345");
        usuarios.save(new Usuario("Admin", "admin@teste.local", "11000000000", hash, Papel.ADMIN));
        var u = usuarios.save(new Usuario("Professor", "professor@teste.local", "11000000000", hash, Papel.PROFESSOR));
        professorId = professores.save(new Professor(u, Set.of(c1))).getId();
        admin = entrar("admin@teste.local"); professor = entrar("professor@teste.local");
    }
    Map<String, Object> cadastro(String email) {
        return Map.of("nome", "Aluno Teste", "email", email, "telefone", "11999999999", "senha", "Teste@12345", "cursoId", curso1);
    }
    JsonNode alunoNovo() throws Exception { return api("POST", "/api/auth/register", null, cadastro("aluno@teste.local"), 201); }
    JsonNode aprovar(Long id) throws Exception { return api("POST", "/api/admin/solicitacoes/" + id + "/aprovar", admin, null, 201); }
    String entrar(String email) throws Exception {
        return api("POST", "/api/auth/login", null, Map.of("email", email, "senha", "Teste@12345"), 200).path("token").asText();
    }
    JsonNode api(String metodo, String caminho, String token, Object corpo, int esperado) throws Exception {
        var requisicao = request(HttpMethod.valueOf(metodo), caminho).contentType(MediaType.APPLICATION_JSON);
        if (token != null) requisicao.header("Authorization", "Bearer " + token);
        if (corpo != null) requisicao.content(json.writeValueAsBytes(corpo));
        var resposta = mvc.perform(requisicao).andExpect(status().is(esperado)).andReturn().getResponse();
        return resposta.getContentAsByteArray().length == 0 ? json.nullNode() : json.readTree(resposta.getContentAsByteArray());
    }
    Map<String, Object> dados(String nome) { return Map.of("nome", nome, "email", "aluno@teste.local", "telefone", "11999999999"); }
    Map<String, Object> professorNovo(List<Long> ids) {
        return Map.of("nome", "Professor Novo", "email", "novo@teste.local", "telefone", "11999999999", "senha", "Teste@12345", "cursoIds", ids);
    }

    @Test void c01CadastroValidoCriaSomenteAlunoPendente() throws Exception {
        var entrada = new HashMap<>(cadastro(" ALUNO@TESTE.LOCAL "));
        var a = api("POST", "/api/auth/register", null, entrada, 201);
        assertThat(a.path("email").asText()).isEqualTo("aluno@teste.local");
        assertThat(a.path("situacaoSolicitacao").asText()).isEqualTo("PENDENTE");
        assertThat(db.queryForObject("select count(*) from matricula", Long.class)).isZero();
        var u = usuarios.findByEmail("aluno@teste.local").orElseThrow();
        assertThat(u.getPapel()).isEqualTo(Papel.ALUNO);
        assertThat(u.getSenhaHash()).startsWith("$2a$12$").isNotEqualTo("Teste@12345");
        assertThat(a.has("senhaHash")).isFalse();
    }
    @Test void c02RejeitaDadosInvalidosECamposPrivilegiados() throws Exception {
        var invalido = new HashMap<>(cadastro("invalido")); invalido.put("nome", " "); invalido.put("senha", "curta");
        var erro = api("POST", "/api/auth/register", null, invalido, 400);
        assertThat(erro.path("codigo").asText()).isEqualTo("DADOS_INVALIDOS");
        assertThat(erro.path("campos").toString()).contains("branco");
        var papel = new HashMap<>(cadastro("aluno@teste.local")); papel.put("papel", "ADMIN");
        api("POST", "/api/auth/register", null, papel, 400);
        assertThat(db.queryForObject("select count(*) from aluno", Long.class)).isZero();
    }
    @Test void c03EmailDuplicadoInclusiveAposExclusao() throws Exception {
        var a = alunoNovo(); api("DELETE", "/api/alunos/" + a.path("id").asLong(), admin, null, 204);
        assertThat(api("POST", "/api/auth/register", null, cadastro("ALUNO@TESTE.LOCAL"), 409).path("codigo").asText()).isEqualTo("EMAIL_JA_CADASTRADO");
    }
    @Test void c04ConsultaAlunoExistente() throws Exception {
        var a = alunoNovo();
        assertThat(api("GET", "/api/alunos/" + a.path("id"), admin, null, 200).path("usuarioId").asLong()).isEqualTo(a.path("usuarioId").asLong());
        assertThat(api("GET", "/api/alunos", admin, null, 200).size()).isEqualTo(1);
        assertThat(api("GET", "/api/admin/solicitacoes/" + a.path("id"), admin, null, 200).path("id").asLong()).isEqualTo(a.path("id").asLong());
    }
    @Test void c05IdInexistenteRetorna404() throws Exception {
        for (var rota : List.of("/api/alunos/999999", "/api/professores/999999", "/api/matriculas/999999", "/api/admin/solicitacoes/999999"))
            assertThat(api("GET", rota, admin, null, 404).path("codigo").asText()).isEqualTo("RECURSO_NAO_ENCONTRADO");
        api("GET", "/api/alunos/nao-numero", admin, null, 400);
    }
    @Test void c06AtualizaAlunoSemAlterarCursoOuPapel() throws Exception {
        var a = alunoNovo(); var id = a.path("id").asLong();
        assertThat(api("PUT", "/api/alunos/" + id, admin, dados("Nome alterado"), 200).path("nome").asText()).isEqualTo("Nome alterado");
        var proibido = new HashMap<>(dados("Aluno")); proibido.put("situacaoSolicitacao", "APROVADO");
        api("PUT", "/api/alunos/" + id, admin, proibido, 400);
        assertThat(db.queryForObject("select situacao_solicitacao from aluno where id=?", String.class, id)).isEqualTo("PENDENTE");
    }
    @Test void c07ExclusaoLogicaPreservaEOmite() throws Exception {
        var a = alunoNovo(); String token = entrar("aluno@teste.local");
        api("DELETE", "/api/alunos/" + a.path("id"), admin, null, 204);
        assertThat(db.queryForObject("select count(*) from aluno", Long.class)).isEqualTo(1);
        assertThat(db.queryForObject("select excluido_em from usuario where id=?", java.time.OffsetDateTime.class, a.path("usuarioId").asLong())).isNotNull();
        assertThat(api("GET", "/api/alunos", admin, null, 200).size()).isZero();
        api("GET", "/api/alunos/" + a.path("id"), admin, null, 404);
        api("GET", "/api/me", token, null, 401);
    }
    @Test void c08ExclusaoBloqueadaComMatriculaAtiva() throws Exception {
        var a = alunoNovo(); aprovar(a.path("id").asLong());
        assertThat(api("DELETE", "/api/alunos/" + a.path("id"), admin, null, 409).path("codigo").asText()).isEqualTo("MATRICULA_ATIVA");
    }
    @Test void c09ProfessorComVariosCursos() throws Exception {
        var p = api("POST", "/api/professores", admin, professorNovo(List.of(curso1, curso2)), 201);
        assertThat(p.path("cursos").size()).isEqualTo(2);
        api("GET", "/api/professores/" + p.path("id"), admin, null, 200);
        assertThat(db.queryForObject("select count(*) from professor_curso where professor_id=?", Long.class, p.path("id").asLong())).isEqualTo(2);
    }
    @Test void c10AtualizaConjuntoCompletoCursosProfessor() throws Exception {
        var corpo = Map.of("nome", "Professor Editado", "email", "professor@teste.local", "telefone", "11000000000", "cursoIds", List.of(curso2));
        assertThat(api("PUT", "/api/professores/" + professorId, admin, corpo, 200).path("cursos").get(0).path("id").asLong()).isEqualTo(curso2);
        assertThat(db.queryForObject("select count(*) from curso", Long.class)).isEqualTo(3);
    }
    @Test void c11RejeitaCursosInvalidosDuplicadosInativosSemEfeitoParcial() throws Exception {
        for (var ids : List.of(List.of(curso1, 999999L), List.of(curso1, curso1), List.of(inativo)))
            api("POST", "/api/professores", admin, professorNovo(ids), 400);
        var corpo = Map.of("nome", "Nao salvar", "email", "professor@teste.local", "telefone", "11000000000", "cursoIds", List.of(curso2, 999999L));
        api("PUT", "/api/professores/" + professorId, admin, corpo, 400);
        assertThat(usuarios.findByEmail("novo@teste.local")).isEmpty();
        assertThat(usuarios.findByEmail("professor@teste.local").orElseThrow().getNome()).isEqualTo("Professor");
        assertThat(db.queryForObject("select curso_id from professor_curso where professor_id=?", Long.class, professorId)).isEqualTo(curso1);
    }
    @Test void c12ProfessorSemCursosRetorna400() throws Exception {
        api("POST", "/api/professores", admin, professorNovo(List.of()), 400);
        assertThat(api("GET", "/api/professores", admin, null, 200).size()).isEqualTo(1);
    }
    @Test void c13AprovacaoCriaMatriculaAtomicaNoCursoSolicitado() throws Exception {
        var a = alunoNovo(); var m = aprovar(a.path("id").asLong());
        assertThat(m.path("situacao").asText()).isEqualTo("ATIVA");
        assertThat(m.path("curso").path("id").asLong()).isEqualTo(curso1);
        assertThat(api("GET", "/api/alunos/" + a.path("id"), admin, null, 200).path("situacaoSolicitacao").asText()).isEqualTo("APROVADO");
    }
    @Test void c14FalhaRealDeBancoDesfazAprovacao() throws Exception {
        var a = alunoNovo();
        db.execute("alter table matricula add constraint falha_matricula_teste check (curso_id <> " + curso1 + ")");
        try { api("POST", "/api/admin/solicitacoes/" + a.path("id") + "/aprovar", admin, null, 409); }
        finally { db.execute("alter table matricula drop constraint falha_matricula_teste"); }
        assertThat(db.queryForObject("select situacao_solicitacao from aluno where id=?", String.class, a.path("id").asLong())).isEqualTo("PENDENTE");
        assertThat(db.queryForObject("select count(*) from matricula", Long.class)).isZero();
    }
    @Test void c15SegundaAprovacaoOuRematriculaImpossivel() throws Exception {
        var a = alunoNovo(); var m = aprovar(a.path("id").asLong());
        api("POST", "/api/admin/solicitacoes/" + a.path("id") + "/aprovar", admin, null, 409);
        api("DELETE", "/api/matriculas/" + m.path("id"), admin, null, 204);
        api("POST", "/api/admin/solicitacoes/" + a.path("id") + "/aprovar", admin, null, 409);
        assertThat(db.queryForObject("select count(*) from matricula", Long.class)).isEqualTo(1);
    }
    @Test void c16AprovacoesConcorrentesCriamUmaUnicaMatricula() throws Exception {
        Long id = alunoNovo().path("id").asLong();
        var prontos = new CountDownLatch(2); var inicio = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            Callable<Integer> tarefa = () -> {
                prontos.countDown(); if (!inicio.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Sem início concorrente");
                try { alunos.aprovar(id); return 201; } catch (RegraException e) { return e.getStatus().value(); }
            };
            var primeira = executor.submit(tarefa); var segunda = executor.submit(tarefa);
            assertThat(prontos.await(10, TimeUnit.SECONDS)).isTrue(); inicio.countDown();
            assertThat(List.of(primeira.get(15, TimeUnit.SECONDS), segunda.get(15, TimeUnit.SECONDS))).containsExactlyInAnyOrder(201, 409);
        }
        assertThat(db.queryForObject("select count(*) from matricula where aluno_id=?", Long.class, id)).isEqualTo(1);
    }
    @Test void c17RejeicaoSemMatriculaESemRepeticao() throws Exception {
        var a = alunoNovo();
        assertThat(api("POST", "/api/admin/solicitacoes/" + a.path("id") + "/rejeitar", admin, null, 200).path("situacaoSolicitacao").asText()).isEqualTo("REJEITADO");
        api("POST", "/api/admin/solicitacoes/" + a.path("id") + "/rejeitar", admin, null, 409);
        assertThat(db.queryForObject("select count(*) from matricula", Long.class)).isZero();
        String token = entrar("aluno@teste.local");
        api("GET", "/api/me/solicitacao", token, null, 200); api("GET", "/api/me/matricula", token, null, 404);
        api("PUT", "/api/me", token, dados("Bloqueado"), 403);
    }
    @Test void c18CancelamentoPreservaAprovadoEPermiteExclusaoPosterior() throws Exception {
        var a = alunoNovo(); var m = aprovar(a.path("id").asLong());
        api("DELETE", "/api/matriculas/" + m.path("id"), admin, null, 204);
        var cancelada = api("GET", "/api/matriculas/" + m.path("id"), admin, null, 200);
        assertThat(cancelada.path("situacao").asText()).isEqualTo("CANCELADA");
        assertThat(cancelada.path("canceladoEm").asText()).endsWith("Z");
        assertThat(db.queryForObject("select situacao_solicitacao from aluno where id=?", String.class, a.path("id").asLong())).isEqualTo("APROVADO");
        api("DELETE", "/api/matriculas/" + m.path("id"), admin, null, 409);
        api("DELETE", "/api/alunos/" + a.path("id"), admin, null, 204);
    }
    @Test void c19CanceladoConsultaEstadoMasNaoAtualizaPerfil() throws Exception {
        var a = alunoNovo(); String token = entrar("aluno@teste.local"); var m = aprovar(a.path("id").asLong());
        api("PUT", "/api/me", token, dados("Autorizado"), 200);
        api("DELETE", "/api/matriculas/" + m.path("id"), admin, null, 204);
        api("GET", "/api/me/solicitacao", token, null, 200); api("GET", "/api/me/matricula", token, null, 200);
        api("PUT", "/api/me", token, dados("Bloqueado"), 403);
        assertThat(api("GET", "/api/professores/me/cursos/" + curso1 + "/alunos", professor, null, 200).size()).isZero();
    }
    @Test void c20ProtecaoDePerfisEPropriedade() throws Exception {
        alunoNovo(); String token = entrar("aluno@teste.local");
        for (var rota : List.of("/api/admin/dashboard", "/api/alunos", "/api/professores", "/api/matriculas")) {
            api("GET", rota, token, null, 403); api("GET", rota, professor, null, 403); api("GET", rota, null, null, 401);
        }
        api("GET", "/api/professores/me/cursos", token, null, 403);
        api("GET", "/api/me/solicitacao", professor, null, 403);
        api("PUT", "/api/me", token, dados("Pendente bloqueado"), 403);
    }
    @Test void c21ProfessorNaoConsultaCursoAlheioENaoVeDadosExcessivos() throws Exception {
        var a = alunoNovo(); aprovar(a.path("id").asLong());
        api("GET", "/api/professores/me/cursos/" + curso2 + "/alunos", professor, null, 403);
        var lista = api("GET", "/api/professores/me/cursos/" + curso1 + "/alunos", professor, null, 200);
        assertThat(lista.size()).isEqualTo(1); assertThat(lista.get(0).has("email")).isFalse(); assertThat(lista.get(0).has("telefone")).isFalse();
        assertThat(api("GET", "/api/professores/me/cursos", professor, null, 200).size()).isEqualTo(1);
    }
    @Test void c22ContaDesativadaInvalidaTokenAnteriorMasNaoExclui() throws Exception {
        var a = alunoNovo(); String token = entrar("aluno@teste.local");
        api("PATCH", "/api/alunos/" + a.path("id") + "/status", admin, Map.of("ativo", false), 200);
        api("GET", "/api/me", token, null, 401); api("POST", "/api/auth/login", null, Map.of("email", "aluno@teste.local", "senha", "Teste@12345"), 401);
        assertThat(api("GET", "/api/alunos", admin, null, 200).size()).isEqualTo(1);
        api("PATCH", "/api/alunos/" + a.path("id") + "/status", admin, Map.of("ativo", true), 200);
        api("GET", "/api/me", token, null, 200);
    }
    @Test void c23DashboardRefleteMutacoesReais() throws Exception {
        var a = alunoNovo();
        var antes = api("GET", "/api/admin/dashboard", admin, null, 200);
        assertThat(antes.path("solicitacoesPendentes").asLong()).isEqualTo(1);
        assertThat(antes.path("cursosDisponiveis").asLong()).isEqualTo(2);
        var m = aprovar(a.path("id").asLong());
        assertThat(api("GET", "/api/admin/dashboard", admin, null, 200).path("alunosAtivos").asLong()).isEqualTo(1);
        api("PATCH", "/api/alunos/" + a.path("id") + "/status", admin, Map.of("ativo", false), 200);
        var desativado = api("GET", "/api/admin/dashboard", admin, null, 200);
        assertThat(desativado.path("alunosAtivos").asLong()).isZero(); assertThat(desativado.path("matriculasAtivas").asLong()).isEqualTo(1);
        api("DELETE", "/api/matriculas/" + m.path("id"), admin, null, 204);
        assertThat(api("GET", "/api/admin/dashboard", admin, null, 200).path("matriculasAtivas").asLong()).isZero();
    }
    @Test void cursoInativoAntigoPodeSerPreservadoMasNovoNao() throws Exception {
        db.update("update curso set ativo=false where id=?", curso1);
        var dados = Map.of("nome", "Novo nome", "email", "professor@teste.local", "telefone", "11000000000");
        assertThat(api("PUT", "/api/professores/" + professorId, admin, dados, 200).path("cursos").size()).isEqualTo(1);
        var comCursos = new HashMap<String, Object>(dados); comCursos.put("cursoIds", List.of(curso1));
        api("PUT", "/api/professores/" + professorId, admin, comCursos, 200);
        comCursos.put("cursoIds", List.of(curso2)); api("PUT", "/api/professores/" + professorId, admin, comCursos, 200);
        comCursos.put("cursoIds", List.of(curso1)); api("PUT", "/api/professores/" + professorId, admin, comCursos, 400);
    }
    @Test void atualizaCursoMatriculaSemMudarSolicitacaoOuCriarRegistro() throws Exception {
        var a = alunoNovo(); var m = aprovar(a.path("id").asLong());
        var atualizada = api("PUT", "/api/matriculas/" + m.path("id"), admin, Map.of("cursoId", curso2), 200);
        assertThat(atualizada.path("id").asLong()).isEqualTo(m.path("id").asLong()); assertThat(atualizada.path("curso").path("id").asLong()).isEqualTo(curso2);
        assertThat(db.queryForObject("select curso_solicitado_id from aluno where id=?", Long.class, a.path("id").asLong())).isEqualTo(curso1);
        api("DELETE", "/api/matriculas/" + m.path("id"), admin, null, 204);
        api("PUT", "/api/matriculas/" + m.path("id"), admin, Map.of("cursoId", curso1), 409);
    }
    @Test void exclusaoProfessorNaoApagaCursosOuAlunos() throws Exception {
        alunoNovo(); api("PATCH", "/api/professores/" + professorId + "/status", admin, Map.of("ativo", false), 200);
        api("GET", "/api/me", professor, null, 401);
        api("DELETE", "/api/professores/" + professorId, admin, null, 204);
        assertThat(api("GET", "/api/professores", admin, null, 200).size()).isZero();
        assertThat(db.queryForObject("select count(*) from curso", Long.class)).isEqualTo(3);
        assertThat(db.queryForObject("select count(*) from aluno", Long.class)).isEqualTo(1);
    }
    @Test void rollbackRealCadastroProfessorQuandoBancoFalha() throws Exception {
        db.execute("alter table professor add constraint falha_professor_teste check (usuario_id < 0) nocheck");
        try { api("POST", "/api/professores", admin, professorNovo(List.of(curso1)), 409); }
        finally { db.execute("alter table professor drop constraint falha_professor_teste"); }
        assertThat(usuarios.findByEmail("novo@teste.local")).isEmpty();
        assertThat(db.queryForObject("select count(*) from professor", Long.class)).isEqualTo(1);
    }
    @Test void senhaMultibyteRespeitaLimiteBcrypt() throws Exception {
        var dados = new HashMap<>(cadastro("aluno@teste.local")); dados.put("senha", "😀".repeat(19));
        api("POST", "/api/auth/register", null, dados, 400);
        dados.put("senha", "😀".repeat(18)); api("POST", "/api/auth/register", null, dados, 201);
    }
    @Test void swaggerCursosPublicosEConsoleH2Desabilitado() throws Exception {
        assertThat(api("GET", "/api/cursos", null, null, 200).size()).isEqualTo(2);
        var doc = api("GET", "/v3/api-docs", null, null, 200);
        assertThat(doc.path("components").path("securitySchemes").has("bearerAuth")).isTrue();
        assertThat(doc.path("paths").has("/api/admin/solicitacoes/{alunoId}/aprovar")).isTrue();
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        api("GET", "/h2-console", admin, null, 403);
    }
    @Test void esquemaPossuiExatamenteSeisTabelasEUniqueRealDeMatricula() throws Exception {
        assertThat(db.queryForList("select lower(table_name) from information_schema.tables where table_schema='PUBLIC'", String.class))
                .containsExactlyInAnyOrder("usuario", "aluno", "professor", "curso", "matricula", "professor_curso");
        var a = alunoNovo(); aprovar(a.path("id").asLong());
        assertThatThrownBy(() -> db.update("insert into matricula(aluno_id,curso_id,situacao,matriculado_em) values(?,?,'ATIVA',CURRENT_TIMESTAMP)", a.path("id").asLong(), curso1))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }
    @Test void cadastroTransacionalDesfazUsuarioQuandoAlunoFalhaNoBanco() throws Exception {
        db.execute("alter table aluno add constraint falha_aluno_teste check (curso_solicitado_id < 0)");
        try { api("POST", "/api/auth/register", null, cadastro("aluno@teste.local"), 409); }
        finally { db.execute("alter table aluno drop constraint falha_aluno_teste"); }
        assertThat(usuarios.findByEmail("aluno@teste.local")).isEmpty();
    }
    @Test void tokenAdulteradoEContaAtualDeterminandoPapel() throws Exception {
        api("GET", "/api/me", admin.substring(0, admin.length() - 10) + "adulterado", null, 401);
        db.update("update usuario set papel='ALUNO' where email='professor@teste.local'");
        api("GET", "/api/professores/me/cursos", professor, null, 403);
        assertThat(api("GET", "/api/me", professor, null, 200).path("papel").asText()).isEqualTo("ALUNO");
    }
    @Test void loginComSenhaLongaOuCredenciaisErradasNaoGera500() throws Exception {
        api("POST", "/api/auth/login", null, Map.of("email", "admin@teste.local", "senha", "senha-incorreta"), 401);
        api("POST", "/api/auth/login", null, Map.of("email", "ausente@teste.local", "senha", "senha-incorreta"), 401);
        api("POST", "/api/auth/login", null, Map.of("email", "admin@teste.local", "senha", "😀".repeat(19)), 400);
    }
}
