package br.com.academico.servico;

import br.com.academico.dominio.*;
import br.com.academico.erro.RegraException;
import br.com.academico.repositorio.MatriculaRepositorio;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaServicoTest {
    @Mock MatriculaRepositorio matriculas;
    @Mock AlunoServico alunos;
    @InjectMocks MatriculaServico servico;
    Matricula preparar() {
        var a = new Aluno(new Usuario("Aluno", "a@teste.local", "11", "hash", Papel.ALUNO), new Curso("C", "D", true)); a.aprovar();
        var m = new Matricula(a, a.getCursoSolicitado());
        when(matriculas.buscarAlunoId(1L)).thenReturn(Optional.of(2L)); when(matriculas.findById(1L)).thenReturn(Optional.of(m)); return m;
    }
    @Test void cancelarTravaAlunoEPreservaAprovacao() {
        var m = preparar(); servico.cancelar(1L);
        assertThat(m.getSituacao()).isEqualTo(SituacaoMatricula.CANCELADA);
        assertThat(m.getCanceladoEm()).isNotNull(); assertThat(m.getAluno().getSituacaoSolicitacao()).isEqualTo(SituacaoSolicitacao.APROVADO);
        var ordem = inOrder(matriculas, alunos); ordem.verify(matriculas).buscarAlunoId(1L); ordem.verify(alunos).bloquear(2L); ordem.verify(matriculas).findById(1L);
    }
    @Test void repeticaoConflita() {
        var m = preparar(); m.cancelar(); assertThatThrownBy(() -> servico.cancelar(1L)).isInstanceOf(RegraException.class);
    }
    @Test void atualizarNaoCriaRegistro() {
        var m = preparar(); var destino = new Curso("Novo", "D", true); when(alunos.cursoDisponivel(3L)).thenReturn(destino);
        servico.atualizar(1L, 3L); assertThat(m.getCurso()).isSameAs(destino); verify(matriculas, never()).save(any());
    }
}
