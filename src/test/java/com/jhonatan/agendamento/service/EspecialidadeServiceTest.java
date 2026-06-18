package com.jhonatan.agendamento.service;

import com.jhonatan.agendamento.dto.request.EspecialidadeRequest;
import com.jhonatan.agendamento.dto.response.EspecialidadeResponse;
import com.jhonatan.agendamento.exception.ConflitoException;
import com.jhonatan.agendamento.exception.RecursoNaoEncontradoException;
import com.jhonatan.agendamento.model.Especialidade;
import com.jhonatan.agendamento.repository.EspecialidadeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EspecialidadeServiceTest {

    @Mock
    private EspecialidadeRepository repository;

    @InjectMocks
    private EspecialidadeService service;

    @Test
    void deveCriarEspecialidadeComSucesso() {
        EspecialidadeRequest request = new EspecialidadeRequest(
                "Podologia",
                "Tratamentos para os pés"
        );

        when(repository.existsByNome(request.nome())).thenReturn(false);

        when(repository.save(any(Especialidade.class)))
                .thenAnswer(invocation -> {
                    Especialidade especialidade = invocation.getArgument(0);
                    especialidade.setId(1L);
                    return especialidade;
                });

        EspecialidadeResponse response = service.criar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Podologia", response.nome());

        verify(repository).save(any(Especialidade.class));
    }

    @Test
    void deveBloquearCriacaoComNomeDuplicado() {
        EspecialidadeRequest request = new EspecialidadeRequest(
                "Podologia",
                "Tratamentos para os pés"
        );

        when(repository.existsByNome(request.nome())).thenReturn(true);

        assertThrows(
                ConflitoException.class,
                () -> service.criar(request)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void deveBuscarEspecialidadePorIdComSucesso() {
        Especialidade especialidade = new Especialidade();
        especialidade.setId(1L);
        especialidade.setNome("Podologia");
        especialidade.setDescricao("Tratamentos para os pés");
        especialidade.setAtivo(true);

        when(repository.findByIdAndAtivoTrue(1L))
                .thenReturn(Optional.of(especialidade));

        EspecialidadeResponse response = service.buscarPorId(1L);

        assertEquals(1L, response.id());
        assertEquals("Podologia", response.nome());
    }

    @Test
    void deveLancarErroQuandoEspecialidadeNaoExiste() {
        when(repository.findByIdAndAtivoTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(1L)
        );
    }

    @Test
    void deveDesativarEspecialidadeComSucesso() {
        Especialidade especialidade = new Especialidade();
        especialidade.setId(1L);
        especialidade.setAtivo(true);

        when(repository.findByIdAndAtivoTrue(1L))
                .thenReturn(Optional.of(especialidade));

        service.desativar(1L);

        assertFalse(especialidade.getAtivo());

        verify(repository).save(especialidade);
    }
}