package br.com.academico.servico;

import br.com.academico.dto.Requisicoes.Login;
import br.com.academico.dto.Respostas.*;
import br.com.academico.erro.RegraException;
import br.com.academico.repositorio.UsuarioRepositorio;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoServico {
    private final UsuarioRepositorio usuarios;
    private final PasswordEncoder senhas;
    private final JwtEncoder jwt;
    private final String hashAusente;
    public AutenticacaoServico(UsuarioRepositorio usuarios, PasswordEncoder senhas, JwtEncoder jwt) {
        this.usuarios = usuarios; this.senhas = senhas; this.jwt = jwt;
        this.hashAusente = senhas.encode("conta-ficticia-para-tempo-de-verificacao");
    }
    @Transactional(readOnly = true)
    public TokenDto autenticar(Login dados) {
        var usuario = usuarios.findByEmail(dados.email());
        boolean confere = senhas.matches(dados.senha(), usuario.map(u -> u.getSenhaHash()).orElse(hashAusente));
        if (usuario.isEmpty() || !confere || !usuario.get().habilitado())
            throw new RegraException(HttpStatus.UNAUTHORIZED, "CREDENCIAIS_INVALIDAS", "E-mail ou senha inválidos, ou conta indisponível.");
        var agora = Instant.now(); var expira = agora.plusSeconds(900);
        var claims = JwtClaimsSet.builder().issuer("academico-beta").subject(usuario.get().getId().toString())
                .issuedAt(agora).expiresAt(expira).build();
        var token = jwt.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new TokenDto(token, "Bearer", expira, PerfilDto.de(usuario.get()));
    }
}
