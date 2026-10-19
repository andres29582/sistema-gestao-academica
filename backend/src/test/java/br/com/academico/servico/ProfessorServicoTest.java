package br.com.academico.servico;

import br.com.academico.dominio.*;
import br.com.academico.dto.Requisicoes.*;
import br.com.academico.erro.RegraException;
import br.com.academico.repositorio.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServicoTest {
    @Mock ProfessorRepositorio professores;
    @Mock CursoRepositorio cursos;
    @Mock MatriculaRepositorio matriculas;
    @Mock ContaServico contas;
    @InjectMocks ProfessorServico servico;
    CriarProfessor dados(List<Long> ids) { return new CriarProfessor("Professor", "p@teste.local", "11000000000", "Teste@123", ids); }
    @Test void exigeCurso() {
        assertThatThrownBy(() -> servico.cadastrar(dados(List.of()))).isInstanceOf(RegraException.class);
        verifyNoInteractions(contas, professores);
    }
    @Test void rejeitaDuplicacaoAntesDePersistir() {
        assertThatThrownBy(() -> servico.cadastrar(dados(List.of(1L, 1L)))).isInstanceOf(RegraException.class);
        verifyNoInteractions(contas, professores, cursos);
    }
    @Test void validaTodosAntesDeCriarUsuario() {
        when(cursos.findById(1L)).thenReturn(Optional.of(new Curso("Curso", "Descrição", true)));
        assertThatThrownBy(() -> servico.cadastrar(dados(List.of(1L, 2L)))).isInstanceOf(RegraException.class);
        verifyNoInteractions(contas, professores);
    }
    @Test void novoVinculoInativoNaoPermitido() {
        when(cursos.findById(1L)).thenReturn(Optional.of(new Curso("Curso", "Descrição", false)));
        assertThatThrownBy(() -> servico.cadastrar(dados(List.of(1L)))).isInstanceOf(RegraException.class);
        verifyNoInteractions(contas);
    }
    @Test void criaProfessorVinculado() {
        when(cursos.findById(1L)).thenReturn(Optional.of(new Curso("Curso", "Descrição", true)));
        when(contas.criar(anyString(), anyString(), anyString(), anyString(), eq(Papel.PROFESSOR)))
                .thenReturn(new Usuario("Professor", "p@teste.local", "11", "hash", Papel.PROFESSOR));
        when(professores.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        assertThat(servico.cadastrar(dados(List.of(1L))).cursos()).hasSize(1);
    }
    @Test void cursoAlheioNaoConsultaAlunos() {
        when(professores.findByUsuarioId(1L)).thenReturn(Optional.of(new Professor(new Usuario("P", "p@teste.local", "11", "hash", Papel.PROFESSOR), Set.of())));
        assertThatThrownBy(() -> servico.alunosDoCurso(1L, 5L)).isInstanceOf(RegraException.class);
        verifyNoInteractions(matriculas);
    }
}
