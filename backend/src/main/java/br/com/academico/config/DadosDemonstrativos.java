package br.com.academico.config;

import br.com.academico.dominio.*;
import br.com.academico.repositorio.*;
import java.util.Set;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component @ConditionalOnProperty(name = "app.demo", havingValue = "true", matchIfMissing = true)
public class DadosDemonstrativos implements ApplicationRunner {
    private final UsuarioRepositorio usuarios;
    private final CursoRepositorio cursos;
    private final AlunoRepositorio alunos;
    private final ProfessorRepositorio professores;
    private final MatriculaRepositorio matriculas;
    private final PasswordEncoder senhas;
    public DadosDemonstrativos(UsuarioRepositorio usuarios, CursoRepositorio cursos, AlunoRepositorio alunos,
            ProfessorRepositorio professores, MatriculaRepositorio matriculas, PasswordEncoder senhas) {
        this.usuarios = usuarios; this.cursos = cursos; this.alunos = alunos; this.professores = professores;
        this.matriculas = matriculas; this.senhas = senhas;
    }
    @Override @Transactional
    public void run(ApplicationArguments args) {
        if (usuarios.count() != 0) return;
        String hash = senhas.encode("Demo@12345");
        var programacao = cursos.save(new Curso("Programação", "Fundamentos de desenvolvimento de software.", true));
        var matematica = cursos.save(new Curso("Matemática", "Raciocínio lógico e matemática aplicada.", true));
        var design = cursos.save(new Curso("Design", "Fundamentos de comunicação visual.", true));
        cursos.save(new Curso("Curso descontinuado", "Disponibilidade encerrada nesta demonstração.", false));
        usuario("Administração Demo", "admin@demo.local", Papel.ADMIN, hash);
        professores.save(new Professor(usuario("Professora Demo", "professor@demo.local", Papel.PROFESSOR, hash), Set.of(programacao, matematica)));
        var professorInativo = usuario("Professor Inativo Demo", "professor.inativo@demo.local", Papel.PROFESSOR, hash);
        professorInativo.setAtivo(false);
        professores.save(new Professor(professorInativo, Set.of(design)));
        aluno("Aluno Pendente Demo", "pendente@demo.local", programacao, hash);
        var ativo = aluno("Aluno Ativo Demo", "aluno@demo.local", programacao, hash);
        ativo.aprovar(); matriculas.save(new Matricula(ativo, programacao));
        var cancelado = aluno("Aluno Cancelado Demo", "cancelado@demo.local", matematica, hash);
        cancelado.aprovar(); var m = matriculas.save(new Matricula(cancelado, matematica)); m.cancelar();
        aluno("Aluno Rejeitado Demo", "rejeitado@demo.local", design, hash).rejeitar();
        var inativo = aluno("Aluno Inativo Demo", "inativo@demo.local", design, hash);
        inativo.aprovar(); inativo.getUsuario().setAtivo(false); matriculas.save(new Matricula(inativo, design));
    }
    private Usuario usuario(String nome, String email, Papel papel, String hash) {
        return usuarios.save(new Usuario(nome, email, "(11) 90000-0000", hash, papel));
    }
    private Aluno aluno(String nome, String email, Curso curso, String hash) {
        return alunos.save(new Aluno(usuario(nome, email, Papel.ALUNO, hash), curso));
    }
}
