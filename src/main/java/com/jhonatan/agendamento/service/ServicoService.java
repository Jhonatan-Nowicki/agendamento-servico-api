package com.jhonatan.agendamento.service;

import com.jhonatan.agendamento.dto.request.ServicoRequest;
import com.jhonatan.agendamento.dto.response.EspecialidadeResponse;
import com.jhonatan.agendamento.dto.response.ServicoResponse;
import com.jhonatan.agendamento.exception.RecursoNaoEncontradoException;
import com.jhonatan.agendamento.model.Especialidade;
import com.jhonatan.agendamento.model.Servico;
import com.jhonatan.agendamento.repository.EspecialidadeRepository;
import com.jhonatan.agendamento.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final EspecialidadeRepository especialidadeRepository;

    public ServicoService(ServicoRepository servicoRepository,
                          EspecialidadeRepository especialidadeRepository) {
        this.servicoRepository = servicoRepository;
        this.especialidadeRepository = especialidadeRepository;
    }

    public ServicoResponse criar(ServicoRequest request) {
        Especialidade especialidade = buscarEspecialidadeAtiva(request.especialidadeId());

        Servico servico = new Servico();
        servico.setNome(request.nome());
        servico.setDescricao(request.descricao());
        servico.setDuracaoMinutos(request.duracaoMinutos());
        servico.setPreco(request.preco());
        servico.setEspecialidade(especialidade);
        servico.setAtivo(true);

        return toResponse(servicoRepository.save(servico));
    }

    public List<ServicoResponse> listar() {
        return servicoRepository.findByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ServicoResponse buscarPorId(Long id) {
        return toResponse(buscarServicoAtivo(id));
    }

    public List<ServicoResponse> listarPorEspecialidade(Long especialidadeId) {
        return servicoRepository.findByEspecialidadeIdAndAtivoTrue(especialidadeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ServicoResponse atualizar(Long id, ServicoRequest request) {
        Servico servico = buscarServicoAtivo(id);
        Especialidade especialidade = buscarEspecialidadeAtiva(request.especialidadeId());

        servico.setNome(request.nome());
        servico.setDescricao(request.descricao());
        servico.setDuracaoMinutos(request.duracaoMinutos());
        servico.setPreco(request.preco());
        servico.setEspecialidade(especialidade);

        return toResponse(servicoRepository.save(servico));
    }

    public void desativar(Long id) {
        Servico servico = buscarServicoAtivo(id);
        servico.setAtivo(false);
        servicoRepository.save(servico);
    }

    private Servico buscarServicoAtivo(Long id) {
        return servicoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado"));
    }

    private Especialidade buscarEspecialidadeAtiva(Long id) {
        return especialidadeRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade não encontrada ou inativa: " + id));
    }

    private ServicoResponse toResponse(Servico servico) {
        return new ServicoResponse(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getDuracaoMinutos(),
                servico.getPreco(),
                servico.getAtivo(),
                new EspecialidadeResponse(
                        servico.getEspecialidade().getId(),
                        servico.getEspecialidade().getNome(),
                        servico.getEspecialidade().getDescricao(),
                        servico.getEspecialidade().getAtivo()
                )
        );
    }
}