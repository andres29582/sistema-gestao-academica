package br.com.academico.dto;

import br.com.academico.dominio.*;
import java.time.Instant;
import java.util.List;

public final class Respostas {
    private Respostas() { }
    public record CursoDto(Long id, String nome, String descricao, boolean ativo) {
        public static CursoDto de(Curso c) { return new CursoDto(c.getId(), c.getNome(), c.getDescricao(), c.isAtivo()); }
    }
    public record PerfilDto(Long usuarioId, String nome, String email, String telefone, Papel papel, boolean ativo) {
        public static PerfilDto de(Usuario u) { return new PerfilDto(u.getId(), u.getNome(), u.getEmail(), u.getTelefone(), u.getPapel(), u.isAtivo()); }
    }
    public record AlunoDto(Long id, Long usuarioId, String nome, String email, String telefone,
            boolean ativo, CursoDto cursoSolicitado, SituacaoSolicitacao situacaoSolicitacao,
            Instant criadoEm, Instant atualizadoEm) {
        public static AlunoDto de(Aluno a) {
            var u = a.getUsuario();
            return new AlunoDto(a.getId(), u.getId(), u.getNome(), u.getEmail(), u.getTelefone(), u.isAtivo(),
                    CursoDto.de(a.getCursoSolicitado()), a.getSituacaoSolicitacao(), a.getCriadoEm(), a.getAtualizadoEm());
        }
    }
    public record ProfessorDto(Long id, Long usuarioId, String nome, String email, String telefone,
            boolean ativo, List<CursoDto> cursos, Instant criadoEm) {
        public static ProfessorDto de(Professor p) {
            var u = p.getUsuario();
            return new ProfessorDto(p.getId(), u.getId(), u.getNome(), u.getEmail(), u.getTelefone(), u.isAtivo(),
                    p.getCursos().stream().sorted(java.util.Comparator.comparing(Curso::getId)).map(CursoDto::de).toList(), p.getCriadoEm());
        }
    }
    public record MatriculaDto(Long id, Long alunoId, String alunoNome, CursoDto curso,
            SituacaoMatricula situacao, Instant matriculadoEm, Instant canceladoEm) {
        public static MatriculaDto de(Matricula m) {
            return new MatriculaDto(m.getId(), m.getAluno().getId(), m.getAluno().getUsuario().getNome(),
                    CursoDto.de(m.getCurso()), m.getSituacao(), m.getMatriculadoEm(), m.getCanceladoEm());
        }
    }
    public record AlunoCursoDto(Long alunoId, String nome, Long matriculaId) {
        public static AlunoCursoDto de(Matricula m) {
            return new AlunoCursoDto(m.getAluno().getId(), m.getAluno().getUsuario().getNome(), m.getId());
        }
    }
    public record TokenDto(String token, String tipo, Instant expiraEm, PerfilDto usuario) { }
    public record DashboardDto(long alunosAtivos, long professoresAtivos, long solicitacoesPendentes,
            long matriculasAtivas, long cursosDisponiveis, List<AlunoDto> solicitacoesRecentes) { }
}
