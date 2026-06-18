package com.jhonatan.agendamento.service;

import com.jhonatan.agendamento.dto.request.ServicoRequest;
import com.jhonatan.agendamento.dto.response.ServicoResponse;
import com.jhonatan.agendamento.exception.RecursoNaoEncontradoException;
import com.jhonatan.agendamento.model.Especialidade;
import com.jhonatan.agendamento.model.Servico;
import com.jhonatan.agendamento.repository.EspecialidadeRepository;
import com.jhonatan.agendamento.repository.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private EspecialidadeRepository especialidadeRepository;

    @InjectMocks
    private ServicoService servicoService;

    private Especialidade especialidade;
    private Servico servico;
    private ServicoRequest request;

    @BeforeEach
    void setUp() {
        especialidade = new Especialidade();
        especialidade.setId(1L);
        especialidade.setNome("Corte");
        especialidade.setDescricao("Corte de cabelo");
        especialidade.setAtivo(true);

        servico = new Servico();
        servico.setId(1L);
        servico.setNome("Corte Masculino");
        servico.setDescricao("Corte tradicional");
        servico.setDuracaoMinutos(30);
        servico.setPreco(BigDecimal.valueOf(35));
        servico.setAtivo(true);
        servico.setEspecialidade(especialidade);

        request = new ServicoRequest(
                "Corte Masculino",
                "Corte tradicional",
                30,
                BigDecimal.valueOf(35),
                1L
        );
    }

    @Test
    void deveCriarServicoComSucesso() {
        when(especialidadeRepository.findByIdAndAtivoTrue(1L))
                .thenReturn(Optional.of(especialidade));

        when(servicoRepository.save(any(Servico.class)))
                .thenAnswer(invocation -> {
                    Servico salvo = invocation.getArgument(0);
                    salvo.setId(1L);
                    return salvo;
                });

        ServicoResponse response = servicoService.criar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Corte Masculino", response.nome());

        verify(servicoRepository).save(any(Servico.class));
    }

    @Test
    void deveBloquearCriacaoQuandoEspecialidadeNaoExiste() {
        when(especialidadeRepository.findByIdAndAtivoTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> servicoService.criar(request)
        );

        verify(servicoRepository, never()).save(any());
    }

    @Test
    void deveBuscarServicoPorIdComSucesso() {
        when(servicoRepository.findByIdAndAtivoTrue(1L))
                .thenReturn(Optional.of(servico));

        ServicoResponse response = servicoService.buscarPorId(1L);

        assertEquals(1L, response.id());
        assertEquals("Corte Masculino", response.nome());
    }

    @Test
    void deveLancarErroQuandoServicoNaoExiste() {
        when(servicoRepository.findByIdAndAtivoTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> servicoService.buscarPorId(1L)
        );
    }

    @Test
    void deveDesativarServicoComSucesso() {
        when(servicoRepository.findByIdAndAtivoTrue(1L))
                .thenReturn(Optional.of(servico));

        servicoService.desativar(1L);

        assertFalse(servico.getAtivo());

        verify(servicoRepository).save(servico);
    }

    @Test
    void deveListarServicosPorEspecialidade() {
        when(servicoRepository.findByEspecialidadeIdAndAtivoTrue(1L))
                .thenReturn(List.of(servico));

        List<ServicoResponse> resultado =
                servicoService.listarPorEspecialidade(1L);

        assertEquals(1, resultado.size());
        assertEquals("Corte Masculino", resultado.get(0).nome());
    }
}