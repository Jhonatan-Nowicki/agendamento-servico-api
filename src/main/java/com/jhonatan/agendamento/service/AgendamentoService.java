package com.jhonatan.agendamento.service;

import com.jhonatan.agendamento.dto.request.AgendamentoRequest;
import com.jhonatan.agendamento.dto.response.*;
import com.jhonatan.agendamento.exception.ConflitoException;
import com.jhonatan.agendamento.exception.RecursoNaoEncontradoException;
import com.jhonatan.agendamento.exception.RegraDeNegocioException;
import com.jhonatan.agendamento.model.*;
import com.jhonatan.agendamento.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final ProfissionalRepository profissionalRepository;
    private final ServicoRepository servicoRepository;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            ClienteRepository clienteRepository,
            ProfissionalRepository profissionalRepository,
            ServicoRepository servicoRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.clienteRepository = clienteRepository;
        this.profissionalRepository = profissionalRepository;
        this.servicoRepository = servicoRepository;
    }

    public AgendamentoResponse criar(AgendamentoRequest request) {
        Cliente cliente = clienteRepository.findByIdAndAtivoTrue(request.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado ou inativo"));

        Profissional profissional = profissionalRepository.findByIdAndAtivoTrue(request.profissionalId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrado ou inativo"));

        Servico servico = servicoRepository.findByIdAndAtivoTrue(request.servicoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado ou inativo"));


        if (!profissionalPossuiEspecialidade(profissional, servico.getEspecialidade().getId())) {
            throw new RegraDeNegocioException("Profissional não possui a especialidade do serviço");
        }

        LocalDateTime inicio = request.dataHoraInicio();

        if (!inicio.isAfter(LocalDateTime.now())) {
            throw new RegraDeNegocioException("Não é permitido agendar no passado");
        }

        LocalDateTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());

        List<Agendamento> conflitos =
                agendamentoRepository.findByProfissionalIdAndStatusAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                        profissional.getId(),
                        StatusAgendamento.AGENDADO,
                        fim,
                        inicio
                );
        if (!conflitos.isEmpty()) {
            throw new ConflitoException("Profissional já possui agendamento neste horário");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setProfissional(profissional);
        agendamento.setServico(servico);
        agendamento.setDataHoraInicio(inicio);
        agendamento.setDataHoraFim(fim);
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        agendamento.setObservacao(request.observacao());

        return toResponse(agendamentoRepository.save(agendamento));
    }

    public List<AgendamentoResponse> listar() {
        return agendamentoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AgendamentoResponse buscarPorId(Long id) {
        Agendamento agendamento = buscarAgendamento(id);
        return toResponse(agendamento);
    }

    public List<AgendamentoResponse> listarPorCliente(Long clienteId) {
        return agendamentoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AgendamentoResponse> listarPorProfissional(Long profissionalId) {
        return agendamentoRepository.findByProfissionalId(profissionalId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AgendamentoResponse cancelar(Long id) {
        Agendamento agendamento = buscarAgendamento(id);

        if (agendamento.getStatus() == StatusAgendamento.CONCLUIDO) {
            throw new RegraDeNegocioException("Não é permitido cancelar agendamento concluído");
        }

        if (agendamento.getDataHoraInicio().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new RegraDeNegocioException("Cancelamento permitido apenas com antecedência mínima de 2 horas");
        }

        agendamento.setStatus(StatusAgendamento.CANCELADO);
        return toResponse(agendamentoRepository.save(agendamento));
    }

    public AgendamentoResponse concluir(Long id) {
        Agendamento agendamento = buscarAgendamento(id);

        if (agendamento.getStatus() == StatusAgendamento.CANCELADO) {
            throw new RegraDeNegocioException("Não é permitido concluir agendamento cancelado");
        }

        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        return toResponse(agendamentoRepository.save(agendamento));
    }

    private Agendamento buscarAgendamento(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado"));
    }

    private boolean profissionalPossuiEspecialidade(Profissional profissional, Long especialidadeId) {
        return profissional.getEspecialidades()
                .stream()
                .anyMatch(especialidade -> especialidade.getId().equals(especialidadeId));
    }

    private AgendamentoResponse toResponse(Agendamento agendamento) {
        return new AgendamentoResponse(
                agendamento.getId(),
                toClienteResponse(agendamento.getCliente()),
                toProfissionalResponse(agendamento.getProfissional()),
                toServicoResponse(agendamento.getServico()),
                agendamento.getDataHoraInicio(),
                agendamento.getDataHoraFim(),
                agendamento.getStatus(),
                agendamento.getObservacao()
        );
    }

    private ClienteResponse toClienteResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getAtivo()
        );
    }

    private ProfissionalResponse toProfissionalResponse(Profissional profissional) {
        List<EspecialidadeResponse> especialidades = profissional.getEspecialidades()
                .stream()
                .map(this::toEspecialidadeResponse)
                .toList();

        return new ProfissionalResponse(
                profissional.getId(),
                profissional.getNome(),
                profissional.getEmail(),
                profissional.getAtivo(),
                especialidades
        );
    }

    private ServicoResponse toServicoResponse(Servico servico) {
        return new ServicoResponse(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getDuracaoMinutos(),
                servico.getPreco(),
                servico.getAtivo(),
                toEspecialidadeResponse(servico.getEspecialidade())
        );
    }

    private EspecialidadeResponse toEspecialidadeResponse(Especialidade especialidade) {
        return new EspecialidadeResponse(
                especialidade.getId(),
                especialidade.getNome(),
                especialidade.getDescricao(),
                especialidade.getAtivo()
        );
    }
}