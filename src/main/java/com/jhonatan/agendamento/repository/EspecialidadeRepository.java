package com.jhonatan.agendamento.repository;

import com.jhonatan.agendamento.model.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, Long id);

    List<Especialidade> findByAtivoTrue();

    Optional<Especialidade> findByIdAndAtivoTrue(Long id);
}