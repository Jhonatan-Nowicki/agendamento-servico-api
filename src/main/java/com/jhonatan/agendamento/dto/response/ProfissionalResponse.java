package com.jhonatan.agendamento.dto.response;

import java.util.List;

public record ProfissionalResponse(
        Long id,
        String nome,
        String email,
        Boolean ativo,
        List<EspecialidadeResponse> especialidades
) {
}