package br.com.academico.repositorio;

import br.com.academico.dominio.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByEmail(String email);
}
