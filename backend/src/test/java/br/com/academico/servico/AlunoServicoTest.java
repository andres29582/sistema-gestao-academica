package br.com.academico.servico;

import br.com.academico.dominio.*;
import br.com.academico.dto.Requisicoes.*;
import br.com.academico.erro.RegraException;
import br.com.academico.repositorio.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServicoTest {
    @Mock AlunoRepositorio alunos;
    @Mock CursoRepositorio cursos;
    @Mock MatriculaRepositorio matriculas;
    @Mock ContaServico contas;
    @InjectMocks AlunoServico servico;
    Aluno aluno;
    Curso curso;
    Usuario usuario;
    @BeforeEach void preparar() {
        usuario = new Usuario("Nome", "aluno@teste.local", "11000000000", "hash", Papel.ALUNO);
        curso = new Curso("Curso", "Descrição", true); aluno = new Aluno(usuario, curso);
    }
    @Test void criaUsuarioAlunoSemMatricula() {
        var dados = new Cadastro("Nome", "aluno@teste.local", "11000000000", "Teste@123", 1L);
        when(cursos.findById(1L)).thenReturn(Optional.of(curso));
        when(contas.criar(anyString(), anyString(), anyString(), anyString(), eq(Papel.ALUNO))).thenReturn(usuario);
        when(alunos.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        assertThat(servico.cadastrar(dados).situacaoSolicitacao()).isEqualTo(SituacaoSolicitacao.PENDENTE);
        verifyNoInteractions(matriculas);
    }
    @Test void cadastroCursoInativoNaoCriaIdentidade() {
        curso.setAtivo(false); when(cursos.findById(1L)).thenReturn(Optional.of(curso));
        assertThatThrownBy(() -> servico.cadastrar(new Cadastro("Nome", "a@teste.local", "11", "Teste@123", 1L))).isInstanceOf(RegraException.class);
        verifyNoInteractions(contas, alunos);
    }
    @Test void aprovacaoAdquireLockAntesDePersistir() {
        when(alunos.bloquear(1L)).thenReturn(Optional.of(aluno));
        when(matriculas.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        var m = servico.aprovar(1L);
        assertThat(m.situacao()).isEqualTo(SituacaoMatricula.ATIVA);
        var ordem = inOrder(alunos, matriculas);
        ordem.verify(alunos).bloquear(1L); ordem.verify(matriculas).existsByAlunoId(1L); ordem.verify(matriculas).saveAndFlush(any());
        assertThat(aluno.getSituacaoSolicitacao()).isEqualTo(SituacaoSolicitacao.APROVADO);
    }
    @Test void naoAprovaNovamente() {
        aluno.aprovar(); when(alunos.bloquear(1L)).thenReturn(Optional.of(aluno));
        assertThatThrownBy(() -> servico.aprovar(1L)).isInstanceOf(RegraException.class).hasMessageContaining("pendentes");
        verifyNoInteractions(matriculas);
    }
    @Test void rejeitarNaoCriaMatricula() {
        when(alunos.bloquear(1L)).thenReturn(Optional.of(aluno));
        assertThat(servico.rejeitar(1L).situacaoSolicitacao()).isEqualTo(SituacaoSolicitacao.REJEITADO);
        verifyNoInteractions(matriculas);
    }
    @Test void excluirComMatriculaAtivaNaoMutaConta() {
        when(alunos.bloquear(1L)).thenReturn(Optional.of(aluno));
        when(matriculas.existsByAlunoIdAndSituacao(1L, SituacaoMatricula.ATIVA)).thenReturn(true);
        assertThatThrownBy(() -> servico.excluir(1L)).isInstanceOf(RegraException.class);
        assertThat(usuario.habilitado()).isTrue(); assertThat(usuario.getExcluidoEm()).isNull();
    }
    @Test void excluirSemMatriculaPreservaRegistro() {
        when(alunos.bloquear(1L)).thenReturn(Optional.of(aluno)); servico.excluir(1L);
        assertThat(usuario.getExcluidoEm()).isNotNull(); assertThat(usuario.isAtivo()).isFalse();
        verify(alunos, never()).delete(any());
    }
    @Test void idDesconhecido() {
        assertThatThrownBy(() -> servico.consultar(999L)).isInstanceOf(RegraException.class);
    }
    @Test void naoPermiteAprovacaoComRegistroDeMatriculaAnterior() {
        when(alunos.bloquear(1L)).thenReturn(Optional.of(aluno)); when(matriculas.existsByAlunoId(1L)).thenReturn(true);
        assertThatThrownBy(() -> servico.aprovar(1L)).isInstanceOf(RegraException.class).hasMessageContaining("já possui");
        verify(matriculas, never()).saveAndFlush(any());
    }
}
