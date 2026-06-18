package com.jhonatan.agendamento.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AgendamentoRequest(
        @NotNull(message = "Cliente é obrigatório")
        Long clienteId,

        @NotNull(message = "Profissional é obrigatório")
        Long profissionalId,

        @NotNull(message = "Serviço é obrigatório")
        Long servicoId,

        @NotNull(message = "Data e hora de início é obrigatória")
        @Future(message = "Data e hora deve ser futura")
        LocalDateTime dataHoraInicio,

        String observacao
) {
}