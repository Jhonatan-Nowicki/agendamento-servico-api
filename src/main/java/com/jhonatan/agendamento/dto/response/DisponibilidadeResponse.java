package com.jhonatan.agendamento.dto.response;

import java.time.LocalDateTime;

public record DisponibilidadeResponse(
        Long agendamentoId,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String status
) {
}