package com.jhonatan.agendamento.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EspecialidadeRequest(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        String descricao
) {
}