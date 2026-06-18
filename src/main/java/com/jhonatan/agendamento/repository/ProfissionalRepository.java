package com.jhonatan.agendamento.repository;

import com.jhonatan.agendamento.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Profissional> findByAtivoTrue();

    Optional<Profissional> findByIdAndAtivoTrue(Long id);
}