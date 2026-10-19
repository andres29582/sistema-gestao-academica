package br.com.academico.seguranca;

import br.com.academico.dominio.Papel;

public record ContaAutenticada(Long usuarioId, Papel papel) { }
