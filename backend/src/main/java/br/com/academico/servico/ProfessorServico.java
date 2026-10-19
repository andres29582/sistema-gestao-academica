package br.com.academico.servico;

import br.com.academico.dominio.*;
import br.com.academico.dto.Requisicoes.*;
import br.com.academico.dto.Respostas.*;
import br.com.academico.erro.RegraException;
import br.com.academico.repositorio.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional(readOnly = true)
public class ProfessorServico {
    private final ProfessorRepositorio professores;
    private final CursoRepositorio cursos;
    private final MatriculaRepositorio matriculas;
    private final ContaServico contas;
    public ProfessorServico(ProfessorRepositorio professores, CursoRepositorio cursos, MatriculaRepositorio matriculas, ContaServico contas) {
        this.professores = professores; this.cursos = cursos; this.matriculas = matriculas; this.contas = contas;
    }
    @Transactional
    public ProfessorDto cadastrar(CriarProfessor dados) {
        var vinculos = validarCursos(dados.cursoIds(), Set.of());
        var usuario = contas.criar(dados.nome(), dados.email(), dados.telefone(), dados.senha(), Papel.PROFESSOR);
        return ProfessorDto.de(professores.saveAndFlush(new Professor(usuario, vinculos)));
    }
    public List<ProfessorDto> listar() {
        return professores.findByUsuarioExcluidoEmIsNullOrderByIdDesc().stream().map(ProfessorDto::de).toList();
    }
    public ProfessorDto consultar(Long id) { return ProfessorDto.de(obter(id)); }
    @Transactional
    public ProfessorDto atualizar(Long id, EditarProfessor dados) {
        var professor = obter(id);
        var vinculos = dados.cursoIds() == null ? null : validarCursos(dados.cursoIds(), professor.getCursos());
        contas.atualizar(professor.getUsuario(), new DadosPessoais(dados.nome(), dados.email(), dados.telefone()));
        if (vinculos != null) professor.substituirCursos(vinculos);
        return ProfessorDto.de(professores.saveAndFlush(professor));
    }
    @Transactional
    public ProfessorDto status(Long id, boolean ativo) {
        var professor = obter(id); professor.getUsuario().setAtivo(ativo); return ProfessorDto.de(professor);
    }
    @Transactional
    public void excluir(Long id) { obter(id).getUsuario().excluir(); }
    public List<CursoDto> meusCursos(Long usuarioId) {
        return porUsuario(usuarioId).getCursos().stream().sorted(Comparator.comparing(Curso::getNome)).map(CursoDto::de).toList();
    }
    public List<AlunoCursoDto> alunosDoCurso(Long usuarioId, Long cursoId) {
        if (porUsuario(usuarioId).getCursos().stream().noneMatch(c -> c.getId().equals(cursoId))) throw RegraException.proibido();
        return matriculas.findByCursoIdAndSituacaoAndAlunoUsuarioAtivoTrueAndAlunoUsuarioExcluidoEmIsNullOrderByAlunoUsuarioNomeAsc(cursoId, SituacaoMatricula.ATIVA)
                .stream().map(AlunoCursoDto::de).toList();
    }
    private Professor porUsuario(Long usuarioId) {
        return professores.findByUsuarioId(usuarioId).orElseThrow(RegraException::naoEncontrado);
    }
    private Professor obter(Long id) {
        var p = professores.findById(id).orElseThrow(RegraException::naoEncontrado);
        if (p.getUsuario().getExcluidoEm() != null) throw RegraException.naoEncontrado();
        return p;
    }
    private Set<Curso> validarCursos(List<Long> ids, Set<Curso> atuais) {
        if (ids == null || ids.isEmpty()) throw RegraException.invalido("CURSOS_OBRIGATORIOS", "Atribua pelo menos um curso.");
        if (new HashSet<>(ids).size() != ids.size()) throw RegraException.invalido("CURSOS_DUPLICADOS", "Não repita cursos na atribuição.");
        var antigos = atuais.stream().map(Curso::getId).collect(java.util.stream.Collectors.toSet());
        Set<Curso> selecionados = new LinkedHashSet<>();
        for (Long id : ids) {
            var c = cursos.findById(id).orElseThrow(() -> RegraException.invalido("CURSO_INVALIDO", "Curso inexistente."));
            if (!c.isAtivo() && !antigos.contains(id)) throw RegraException.invalido("CURSO_INATIVO", "Não é permitido atribuir novo curso inativo.");
            selecionados.add(c);
        }
        return selecionados;
    }
}
