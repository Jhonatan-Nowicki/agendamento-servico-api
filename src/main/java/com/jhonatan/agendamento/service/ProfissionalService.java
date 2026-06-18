package com.jhonatan.agendamento.service;

import com.jhonatan.agendamento.dto.request.ProfissionalRequest;
import com.jhonatan.agendamento.dto.response.DisponibilidadeResponse;
import com.jhonatan.agendamento.dto.response.EspecialidadeResponse;
import com.jhonatan.agendamento.dto.response.ProfissionalResponse;
import com.jhonatan.agendamento.exception.ConflitoException;
import com.jhonatan.agendamento.exception.RecursoNaoEncontradoException;
import com.jhonatan.agendamento.exception.RegraDeNegocioException;
import com.jhonatan.agendamento.model.Especialidade;
import com.jhonatan.agendamento.model.Profissional;
import com.jhonatan.agendamento.model.StatusAgendamento;
import com.jhonatan.agendamento.repository.AgendamentoRepository;
import com.jhonatan.agendamento.repository.EspecialidadeRepository;
import com.jhonatan.agendamento.repository.ProfissionalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final AgendamentoRepository agendamentoRepository;

    public ProfissionalService(ProfissionalRepository profissionalRepository,
                               EspecialidadeRepository especialidadeRepository,
                               AgendamentoRepository agendamentoRepository) {
        this.profissionalRepository = profissionalRepository;
        this.especialidadeRepository = especialidadeRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    public ProfissionalResponse criar(ProfissionalRequest request) {
        if (profissionalRepository.existsByEmail(request.email())) {
            throw new ConflitoException("Já existe um profissional com este email");
        }

        List<Especialidade> especialidades = buscarEspecialidadesAtivas(request.especialidadeIds());

        Profissional profissional = new Profissional();
        profissional.setNome(request.nome());
        profissional.setEmail(request.email());
        profissional.setEspecialidades(especialidades);
        profissional.setAtivo(true);

        return toResponse(profissionalRepository.save(profissional));
    }

    public List<ProfissionalResponse> listar() {
        return profissionalRepository.findByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProfissionalResponse buscarPorId(Long id) {
        Profissional profissional = buscarProfissionalAtivo(id);
        return toResponse(profissional);
    }

    public ProfissionalResponse atualizar(Long id, ProfissionalRequest request) {
        Profissional profissional = buscarProfissionalAtivo(id);

        if (profissionalRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new ConflitoException("Já existe um profissional com este email");
        }

        List<Especialidade> especialidades = buscarEspecialidadesAtivas(request.especialidadeIds());

        profissional.setNome(request.nome());
        profissional.setEmail(request.email());
        profissional.setEspecialidades(especialidades);

        return toResponse(profissionalRepository.save(profissional));
    }

    public void desativar(Long id) {
        Profissional profissional = buscarProfissionalAtivo(id);

        boolean possuiAgendamentoFuturo = agendamentoRepository
                .existsByProfissionalIdAndStatusAndDataHoraInicioAfter(
                        id,
                        StatusAgendamento.AGENDADO,
                        LocalDateTime.now()
                );

        if (possuiAgendamentoFuturo) {
            throw new RegraDeNegocioException(
                    "Não é possível desativar profissional com agendamentos futuros"
            );
        }

        profissional.setAtivo(false);
        profissionalRepository.save(profissional);
    }

    public List<DisponibilidadeResponse> consultarDisponibilidade(Long profissionalId, LocalDate data) {
        buscarProfissionalAtivo(profissionalId);

        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.plusDays(1).atStartOfDay().minusNanos(1);

        return agendamentoRepository.findByProfissionalIdAndStatusAndDataHoraInicioBetween(
                        profissionalId,
                        StatusAgendamento.AGENDADO,
                        inicioDia,
                        fimDia
                )
                .stream()
                .map(agendamento -> new DisponibilidadeResponse(
                        agendamento.getId(),
                        agendamento.getDataHoraInicio(),
                        agendamento.getDataHoraFim(),
                        agendamento.getStatus().name()
                ))
                .toList();
    }

    private Profissional buscarProfissionalAtivo(Long id) {
        return profissionalRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrado"));
    }

    private List<Especialidade> buscarEspecialidadesAtivas(List<Long> ids) {
        return ids.stream()
                .map(id -> especialidadeRepository.findByIdAndAtivoTrue(id)
                        .orElseThrow(() -> new RecursoNaoEncontradoException(
                                "Especialidade não encontrada ou inativa: " + id)))
                .toList();
    }

    private ProfissionalResponse toResponse(Profissional profissional) {
        List<EspecialidadeResponse> especialidades = profissional.getEspecialidades()
                .stream()
                .map(especialidade -> new EspecialidadeResponse(
                        especialidade.getId(),
                        especialidade.getNome(),
                        especialidade.getDescricao(),
                        especialidade.getAtivo()
                ))
                .toList();

        return new ProfissionalResponse(
                profissional.getId(),
                profissional.getNome(),
                profissional.getEmail(),
                profissional.getAtivo(),
                especialidades
        );
    }
}