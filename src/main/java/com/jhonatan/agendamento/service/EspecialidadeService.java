package com.jhonatan.agendamento.service;

import com.jhonatan.agendamento.dto.request.EspecialidadeRequest;
import com.jhonatan.agendamento.dto.response.EspecialidadeResponse;
import com.jhonatan.agendamento.exception.ConflitoException;
import com.jhonatan.agendamento.exception.RecursoNaoEncontradoException;
import com.jhonatan.agendamento.model.Especialidade;
import com.jhonatan.agendamento.repository.EspecialidadeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadeService {

    private final EspecialidadeRepository repository;

    public EspecialidadeService(EspecialidadeRepository repository) {
        this.repository = repository;
    }

    public EspecialidadeResponse criar(EspecialidadeRequest request) {
        if (repository.existsByNome(request.nome())) {
            throw new ConflitoException("Já existe uma especialidade com este nome");
        }

        Especialidade especialidade = new Especialidade();
        especialidade.setNome(request.nome());
        especialidade.setDescricao(request.descricao());
        especialidade.setAtivo(true);

        return toResponse(repository.save(especialidade));
    }

    public List<EspecialidadeResponse> listar() {
        return repository.findByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EspecialidadeResponse buscarPorId(Long id) {
        Especialidade especialidade = repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade não encontrada"));

        return toResponse(especialidade);
    }

    public EspecialidadeResponse atualizar(Long id, EspecialidadeRequest request) {
        Especialidade especialidade = repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade não encontrada"));

        if (repository.existsByNomeAndIdNot(request.nome(), id)) {
            throw new ConflitoException("Já existe uma especialidade com este nome");
        }

        especialidade.setNome(request.nome());
        especialidade.setDescricao(request.descricao());

        return toResponse(repository.save(especialidade));
    }

    public void desativar(Long id) {
        Especialidade especialidade = repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade não encontrada"));

        especialidade.setAtivo(false);
        repository.save(especialidade);
    }

    private EspecialidadeResponse toResponse(Especialidade especialidade) {
        return new EspecialidadeResponse(
                especialidade.getId(),
                especialidade.getNome(),
                especialidade.getDescricao(),
                especialidade.getAtivo()
        );
    }
}