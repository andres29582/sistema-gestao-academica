package br.com.academico.dto;

import jakarta.validation.*;
import java.lang.annotation.*;
import java.nio.charset.StandardCharsets;

@Documented @Constraint(validatedBy = SenhaValida.Validador.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface SenhaValida {
    String message() default "A senha deve conter no mínimo 8 caracteres e no máximo 72 bytes UTF-8.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    class Validador implements ConstraintValidator<SenhaValida, String> {
        @Override public boolean isValid(String senha, ConstraintValidatorContext contexto) {
            return senha != null && senha.codePointCount(0, senha.length()) >= 8
                    && senha.getBytes(StandardCharsets.UTF_8).length <= 72;
        }
    }
}
