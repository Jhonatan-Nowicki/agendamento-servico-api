package com.jhonatan.agendamento.repository;

import com.jhonatan.agendamento.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByAtivoTrue();

    Optional<Servico> findByIdAndAtivoTrue(Long id);

    List<Servico> findByEspecialidadeIdAndAtivoTrue(Long especialidadeId);
}