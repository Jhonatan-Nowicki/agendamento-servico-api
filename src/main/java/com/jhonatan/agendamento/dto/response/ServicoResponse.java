package com.jhonatan.agendamento.dto.response;

import java.math.BigDecimal;

public record ServicoResponse(
        Long id,
        String nome,
        String descricao,
        Integer duracaoMinutos,
        BigDecimal preco,
        Boolean ativo,
        EspecialidadeResponse especialidade
) {
}