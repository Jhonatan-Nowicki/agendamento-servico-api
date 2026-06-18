package com.jhonatan.agendamento.service;

import com.jhonatan.agendamento.dto.request.ClienteRequest;
import com.jhonatan.agendamento.dto.response.ClienteResponse;
import com.jhonatan.agendamento.exception.ConflitoException;
import com.jhonatan.agendamento.exception.RecursoNaoEncontradoException;
import com.jhonatan.agendamento.model.Cliente;
import com.jhonatan.agendamento.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public ClienteResponse criar(ClienteRequest request) {

        if (repository.existsByEmail(request.email())) {
            throw new ConflitoException("Já existe um cliente com este email");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setTelefone(request.telefone());
        cliente.setAtivo(true);

        return toResponse(repository.save(cliente));
    }

    public List<ClienteResponse> listar() {
        return repository.findByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ClienteResponse buscarPorId(Long id) {
        Cliente cliente = repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

        return toResponse(cliente);
    }

    public ClienteResponse atualizar(Long id, ClienteRequest request) {

        Cliente cliente = repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

        if (repository.existsByEmailAndIdNot(request.email(), id)) {
            throw new ConflitoException("Já existe um cliente com este email");
        }

        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setTelefone(request.telefone());

        return toResponse(repository.save(cliente));
    }

    public void desativar(Long id) {

        Cliente cliente = repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

        cliente.setAtivo(false);

        repository.save(cliente);
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getAtivo()
        );
    }
}