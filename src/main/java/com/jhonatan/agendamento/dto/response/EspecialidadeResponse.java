package com.jhonatan.agendamento.dto.response;

public record EspecialidadeResponse(
        Long id,
        String nome,
        String descricao,
        Boolean ativo
) {
}