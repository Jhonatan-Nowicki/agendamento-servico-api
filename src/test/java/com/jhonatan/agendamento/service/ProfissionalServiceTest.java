package com.jhonatan.agendamento.service;

import com.jhonatan.agendamento.dto.request.ProfissionalRequest;
import com.jhonatan.agendamento.dto.response.ProfissionalResponse;
import com.jhonatan.agendamento.exception.ConflitoException;
import com.jhonatan.agendamento.exception.RecursoNaoEncontradoException;
import com.jhonatan.agendamento.exception.RegraDeNegocioException;
import com.jhonatan.agendamento.model.Agendamento;
import com.jhonatan.agendamento.model.Especialidade;
import com.jhonatan.agendamento.model.Profissional;
import com.jhonatan.agendamento.model.StatusAgendamento;
import com.jhonatan.agendamento.repository.AgendamentoRepository;
import com.jhonatan.agendamento.repository.EspecialidadeRepository;
import com.jhonatan.agendamento.repository.ProfissionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfissionalServiceTest {

    @Mock
    private ProfissionalRepository profissionalRepository;

    @Mock
    private EspecialidadeRepository especialidadeRepository;

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @InjectMocks
    private ProfissionalService profissionalService;

    private Especialidade especialidade;
    private Profissional profissional;
    private ProfissionalRequest request;

    @BeforeEach
    void setUp() {
        especialidade = new Especialidade();
        especialidade.setId(1L);
        especialidade.setNome("Corte");
        especialidade.setDescricao("Corte de cabelo");
        especialidade.setAtivo(true);

        profissional = new Profissional();
        profissional.setId(1L);
        profissional.setNome("Carlos Profissional");
        profissional.setEmail("carlos@email.com");
        profissional.setAtivo(true);
        profissional.setEspecialidades(List.of(especialidade));

        request = new ProfissionalRequest(
                "Carlos Profissional",
                "carlos@email.com",
                List.of(1L)
        );
    }

    @Test
    void deveCriarProfissionalComSucesso() {
        when(profissionalRepository.existsByEmail(request.email())).thenReturn(false);
        when(especialidadeRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(especialidade));
        when(profissionalRepository.save(any(Profissional.class))).thenAnswer(invocation -> {
            Profissional salvo = invocation.getArgument(0);
            salvo.setId(1L);
            return salvo;
        });

        ProfissionalResponse response = profissionalService.criar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Carlos Profissional", response.nome());
        assertEquals("carlos@email.com", response.email());
        assertEquals(1, response.especialidades().size());
        verify(profissionalRepository).save(any(Profissional.class));
    }

    @Test
    void deveBloquearCriacaoComEmailDuplicado() {
        when(profissionalRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(ConflitoException.class, () -> profissionalService.criar(request));

        verify(profissionalRepository, never()).save(any(Profissional.class));
    }

    @Test
    void deveBloquearCriacaoComEspecialidadeInexistenteOuInativa() {
        when(profissionalRepository.existsByEmail(request.email())).thenReturn(false);
        when(especialidadeRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> profissionalService.criar(request));

        verify(profissionalRepository, never()).save(any(Profissional.class));
    }

    @Test
    void deveBuscarProfissionalPorIdComSucesso() {
        when(profissionalRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(profissional));

        ProfissionalResponse response = profissionalService.buscarPorId(1L);

        assertEquals(1L, response.id());
        assertEquals("Carlos Profissional", response.nome());
    }

    @Test
    void deveLancarErroAoBuscarProfissionalInexistente() {
        when(profissionalRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> profissionalService.buscarPorId(1L));
    }

    @Test
    void deveAtualizarProfissionalComSucesso() {
        ProfissionalRequest requestAtualizacao = new ProfissionalRequest(
                "Carlos Atualizado",
                "carlos.atualizado@email.com",
                List.of(1L)
        );

        when(profissionalRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(profissional));
        when(profissionalRepository.existsByEmailAndIdNot(requestAtualizacao.email(), 1L)).thenReturn(false);
        when(especialidadeRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(especialidade));
        when(profissionalRepository.save(any(Profissional.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfissionalResponse response = profissionalService.atualizar(1L, requestAtualizacao);

        assertEquals("Carlos Atualizado", response.nome());
        assertEquals("carlos.atualizado@email.com", response.email());
        verify(profissionalRepository).save(profissional);
    }

    @Test
    void deveBloquearAtualizacaoComEmailDuplicado() {
        ProfissionalRequest requestAtualizacao = new ProfissionalRequest(
                "Carlos Atualizado",
                "emailduplicado@email.com",
                List.of(1L)
        );

        when(profissionalRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(profissional));
        when(profissionalRepository.existsByEmailAndIdNot(requestAtualizacao.email(), 1L)).thenReturn(true);

        assertThrows(ConflitoException.class, () -> profissionalService.atualizar(1L, requestAtualizacao));

        verify(profissionalRepository, never()).save(any(Profissional.class));
    }

    @Test
    void deveDesativarProfissionalComSucessoQuandoNaoHaAgendamentoFuturo() {
        when(profissionalRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.existsByProfissionalIdAndStatusAndDataHoraInicioAfter(
                eq(1L),
                eq(StatusAgendamento.AGENDADO),
                any(LocalDateTime.class)
        )).thenReturn(false);

        profissionalService.desativar(1L);

        assertFalse(profissional.getAtivo());
        verify(profissionalRepository).save(profissional);
    }

    @Test
    void deveBloquearDesativacaoQuandoHaAgendamentoFuturo() {
        when(profissionalRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.existsByProfissionalIdAndStatusAndDataHoraInicioAfter(
                eq(1L),
                eq(StatusAgendamento.AGENDADO),
                any(LocalDateTime.class)
        )).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> profissionalService.desativar(1L));

        verify(profissionalRepository, never()).save(any(Profissional.class));
    }

    @Test
    void deveConsultarDisponibilidadeComSucesso() {
        LocalDate data = LocalDate.now().plusDays(1);

        Agendamento agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setProfissional(profissional);
        agendamento.setStatus(StatusAgendamento.AGENDADO);

        when(profissionalRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByProfissionalIdAndStatusAndDataHoraInicioBetween(
                eq(1L),
                eq(StatusAgendamento.AGENDADO),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(agendamento));

        List<Agendamento> resultado = profissionalService.consultarDisponibilidade(1L, data);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }
}