package com.jhonatan.agendamento.service;

import com.jhonatan.agendamento.dto.request.AgendamentoRequest;
import com.jhonatan.agendamento.dto.response.AgendamentoResponse;
import com.jhonatan.agendamento.exception.ConflitoException;
import com.jhonatan.agendamento.exception.RecursoNaoEncontradoException;
import com.jhonatan.agendamento.exception.RegraDeNegocioException;
import com.jhonatan.agendamento.model.*;
import com.jhonatan.agendamento.repository.AgendamentoRepository;
import com.jhonatan.agendamento.repository.ClienteRepository;
import com.jhonatan.agendamento.repository.ProfissionalRepository;
import com.jhonatan.agendamento.repository.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProfissionalRepository profissionalRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Cliente cliente;
    private Profissional profissional;
    private Servico servico;
    private Especialidade especialidade;
    private AgendamentoRequest request;

    @BeforeEach
    void setUp() {
        especialidade = new Especialidade();
        especialidade.setId(1L);
        especialidade.setNome("Corte");
        especialidade.setDescricao("Corte de cabelo");
        especialidade.setAtivo(true);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Pedro Cliente");
        cliente.setEmail("pedro@email.com");
        cliente.setTelefone("41999999999");
        cliente.setAtivo(true);

        profissional = new Profissional();
        profissional.setId(1L);
        profissional.setNome("Carlos Profissional");
        profissional.setEmail("carlos@email.com");
        profissional.setAtivo(true);
        profissional.setEspecialidades(List.of(especialidade));

        servico = new Servico();
        servico.setId(1L);
        servico.setNome("Corte Masculino");
        servico.setDescricao("Corte tradicional");
        servico.setDuracaoMinutos(30);
        servico.setPreco(BigDecimal.valueOf(35));
        servico.setAtivo(true);
        servico.setEspecialidade(especialidade);

        request = new AgendamentoRequest(
                1L,
                1L,
                1L,
                LocalDateTime.now().plusDays(1),
                "Primeiro atendimento"
        );
    }

    @Test
    void deveCriarAgendamentoComSucesso() {
        when(clienteRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(cliente));
        when(profissionalRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(profissional));
        when(servicoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(servico));
        when(agendamentoRepository.findByProfissionalIdAndStatusAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                anyLong(),
                eq(StatusAgendamento.AGENDADO),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of());

        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> {
            Agendamento agendamento = invocation.getArgument(0);
            agendamento.setId(1L);
            return agendamento;
        });

        AgendamentoResponse response = agendamentoService.criar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(StatusAgendamento.AGENDADO, response.status());
        assertEquals(request.dataHoraInicio().plusMinutes(30), response.dataHoraFim());
        verify(agendamentoRepository).save(any(Agendamento.class));
    }
    @Test
    void deveBloquearCriacaoQuandoDataHoraInicioEstiverNoPassado() {
        AgendamentoRequest requestPassado = new AgendamentoRequest(
                1L,
                1L,
                1L,
                LocalDateTime.now().minusHours(1),
                "Agendamento no passado"
        );

        when(clienteRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(cliente));
        when(profissionalRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(profissional));
        when(servicoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(servico));

        assertThrows(RegraDeNegocioException.class, () -> agendamentoService.criar(requestPassado));

        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void deveBloquearCriacaoQuandoExisteConflitoDeHorario() {
        Agendamento agendamentoExistente = new Agendamento();
        agendamentoExistente.setId(99L);

        when(clienteRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(cliente));
        when(profissionalRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(profissional));
        when(servicoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(servico));
        when(agendamentoRepository.findByProfissionalIdAndStatusAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                anyLong(),
                eq(StatusAgendamento.AGENDADO),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(agendamentoExistente));

        assertThrows(ConflitoException.class, () -> agendamentoService.criar(request));

        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void deveBloquearCriacaoQuandoProfissionalNaoPossuiEspecialidadeDoServico() {
        Especialidade outraEspecialidade = new Especialidade();
        outraEspecialidade.setId(2L);
        outraEspecialidade.setNome("Manicure");
        outraEspecialidade.setAtivo(true);

        profissional.setEspecialidades(List.of(outraEspecialidade));

        when(clienteRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(cliente));
        when(profissionalRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(profissional));
        when(servicoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(servico));

        assertThrows(RegraDeNegocioException.class, () -> agendamentoService.criar(request));

        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void deveLancarErroQuandoClienteNaoForEncontrado() {
        when(clienteRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> agendamentoService.criar(request));

        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void deveCancelarAgendamentoComSucesso() {
        Agendamento agendamento = criarAgendamentoSalvo(StatusAgendamento.AGENDADO, LocalDateTime.now().plusHours(3));

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgendamentoResponse response = agendamentoService.cancelar(1L);

        assertEquals(StatusAgendamento.CANCELADO, response.status());
        verify(agendamentoRepository).save(agendamento);
    }

    @Test
    void deveBloquearCancelamentoForaDoPrazoMinimo() {
        Agendamento agendamento = criarAgendamentoSalvo(StatusAgendamento.AGENDADO, LocalDateTime.now().plusHours(1));

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        assertThrows(RegraDeNegocioException.class, () -> agendamentoService.cancelar(1L));

        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void deveBloquearCancelamentoDeAgendamentoConcluido() {
        Agendamento agendamento = criarAgendamentoSalvo(StatusAgendamento.CONCLUIDO, LocalDateTime.now().plusHours(3));

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        assertThrows(RegraDeNegocioException.class, () -> agendamentoService.cancelar(1L));

        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void deveConcluirAgendamentoComSucesso() {
        Agendamento agendamento = criarAgendamentoSalvo(StatusAgendamento.AGENDADO, LocalDateTime.now().plusHours(3));

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgendamentoResponse response = agendamentoService.concluir(1L);

        assertEquals(StatusAgendamento.CONCLUIDO, response.status());
        verify(agendamentoRepository).save(agendamento);
    }

    @Test
    void deveBloquearConclusaoDeAgendamentoCancelado() {
        Agendamento agendamento = criarAgendamentoSalvo(StatusAgendamento.CANCELADO, LocalDateTime.now().plusHours(3));

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        assertThrows(RegraDeNegocioException.class, () -> agendamentoService.concluir(1L));

        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }


    private Agendamento criarAgendamentoSalvo(StatusAgendamento status, LocalDateTime inicio) {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setCliente(cliente);
        agendamento.setProfissional(profissional);
        agendamento.setServico(servico);
        agendamento.setDataHoraInicio(inicio);
        agendamento.setDataHoraFim(inicio.plusMinutes(servico.getDuracaoMinutos()));
        agendamento.setStatus(status);
        agendamento.setObservacao("Teste");
        return agendamento;
    }
}