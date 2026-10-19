package br.com.academico;

import br.com.academico.dominio.Curso;
import br.com.academico.repositorio.*;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import static org.assertj.core.api.Assertions.*;

class ReinicioH2Test {
    @Test void c24FecharContextoDescartaDadosEReinicioRecarregaDemonstracao() {
        Map<String, Object> configuracao = Map.of(
                "server.port", "0", "spring.datasource.url", "jdbc:h2:mem:reinicio-real;DB_CLOSE_ON_EXIT=FALSE",
                "spring.jpa.hibernate.ddl-auto", "create-drop", "logging.level.root", "WARN", "app.demo", "true");
        try (var primeira = new SpringApplicationBuilder(Aplicacao.class).properties(configuracao).run()) {
            var cursos = primeira.getBean(CursoRepositorio.class);
            assertThat(cursos.count()).isEqualTo(4);
            assertThat(primeira.getBean(org.springframework.jdbc.core.JdbcTemplate.class)
                    .queryForObject("select cancelado_em >= matriculado_em from matricula where situacao='CANCELADA'", Boolean.class)).isTrue();
            assertThat(primeira.getBean(UsuarioRepositorio.class).findByEmail("admin@demo.local")).isPresent();
            cursos.save(new Curso("Temporário", "Deve desaparecer", true));
            assertThat(cursos.count()).isEqualTo(5);
        }
        try (var segunda = new SpringApplicationBuilder(Aplicacao.class).properties(configuracao).run()) {
            assertThat(segunda.getBean(CursoRepositorio.class).count()).isEqualTo(4);
            assertThat(segunda.getBean(AlunoRepositorio.class).count()).isEqualTo(5);
            assertThat(segunda.getBean(MatriculaRepositorio.class).count()).isEqualTo(3);
            assertThat(segunda.getBean(UsuarioRepositorio.class).findByEmail("admin@demo.local")).isPresent();
        }
    }
}
