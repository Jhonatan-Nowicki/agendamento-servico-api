package com.jhonatan.agendamento.repository;

import com.jhonatan.agendamento.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Cliente> findByAtivoTrue();

    Optional<Cliente> findByIdAndAtivoTrue(Long id);
}