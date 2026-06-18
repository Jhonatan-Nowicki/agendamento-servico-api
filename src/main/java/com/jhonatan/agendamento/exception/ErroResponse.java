package com.jhonatan.agendamento.exception;

import java.time.LocalDateTime;

public record ErroResponse(
        int status,
        String mensagem,
        LocalDateTime dataHora
) {
}