package com.jhonatan.agendamento.repository;

import com.jhonatan.agendamento.model.Agendamento;
import com.jhonatan.agendamento.model.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByClienteId(Long clienteId);

    List<Agendamento> findByProfissionalId(Long profissionalId);

    List<Agendamento> findByProfissionalIdAndStatusAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
            Long profissionalId,
            StatusAgendamento status,
            LocalDateTime fim,
            LocalDateTime inicio
    );

    List<Agendamento> findByProfissionalIdAndStatusAndDataHoraInicioBetween(
            Long profissionalId,
            StatusAgendamento status,
            LocalDateTime inicio,
            LocalDateTime fim
    );

    boolean existsByProfissionalIdAndStatusAndDataHoraInicioAfter(
            Long profissionalId,
            StatusAgendamento status,
            LocalDateTime agora
    );


}