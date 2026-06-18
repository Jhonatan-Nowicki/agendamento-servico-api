package com.jhonatan.agendamento.dto.response;

public record ClienteResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        Boolean ativo
) {
}