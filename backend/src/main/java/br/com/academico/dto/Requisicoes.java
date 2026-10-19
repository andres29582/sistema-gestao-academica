package br.com.academico.dto;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Locale;

public final class Requisicoes {
    private Requisicoes() { }
    public static String normalizarEmail(String email) {
        return email == null ? null : email.strip().toLowerCase(Locale.ROOT);
    }
    public record Cadastro(@NotBlank @Size(max = 120) String nome,
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(max = 30) String telefone, @NotBlank @SenhaValida String senha,
            @NotNull @Positive Long cursoId) {
        public Cadastro { email = normalizarEmail(email); }
    }
    public record Login(@NotBlank @Email @Size(max = 254) String email,
            @NotBlank @SenhaValida String senha) {
        public Login { email = normalizarEmail(email); }
    }
    public record DadosPessoais(@NotBlank @Size(max = 120) String nome,
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(max = 30) String telefone) {
        public DadosPessoais { email = normalizarEmail(email); }
    }
    public record CriarProfessor(@NotBlank @Size(max = 120) String nome,
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(max = 30) String telefone, @NotBlank @SenhaValida String senha,
            @NotEmpty List<@NotNull @Positive Long> cursoIds) {
        public CriarProfessor { email = normalizarEmail(email); }
    }
    public record EditarProfessor(@NotBlank @Size(max = 120) String nome,
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(max = 30) String telefone, List<@NotNull @Positive Long> cursoIds) {
        public EditarProfessor { email = normalizarEmail(email); }
    }
    public record StatusConta(@NotNull Boolean ativo) { }
    public record AlterarMatricula(@NotNull @Positive Long cursoId) { }
}
