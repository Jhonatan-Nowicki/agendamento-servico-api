package com.jhonatan.agendamento.dto.response;

import com.jhonatan.agendamento.model.StatusAgendamento;

import java.time.LocalDateTime;

public record AgendamentoResponse(
        Long id,
        ClienteResponse cliente,
        ProfissionalResponse profissional,
        ServicoResponse servico,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        StatusAgendamento status,
        String observacao
) {
}