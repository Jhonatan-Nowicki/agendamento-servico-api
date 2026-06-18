package com.jhonatan.agendamento.service;

import com.jhonatan.agendamento.dto.request.ClienteRequest;
import com.jhonatan.agendamento.dto.response.ClienteResponse;
import com.jhonatan.agendamento.exception.ConflitoException;
import com.jhonatan.agendamento.exception.RecursoNaoEncontradoException;
import com.jhonatan.agendamento.model.Cliente;
import com.jhonatan.agendamento.repository.ClienteRepository;
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
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @Test
    void deveCriarClienteComSucesso() {
        ClienteRequest request = new ClienteRequest(
                "João",
                "joao@email.com",
                "41999999999"
        );

        when(repository.existsByEmail(request.email())).thenReturn(false);

        when(repository.save(any(Cliente.class)))
                .thenAnswer(invocation -> {
                    Cliente cliente = invocation.getArgument(0);
                    cliente.setId(1L);
                    return cliente;
                });

        ClienteResponse response = service.criar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("João", response.nome());

        verify(repository).save(any(Cliente.class));
    }

    @Test
    void deveBloquearCriacaoComEmailDuplicado() {
        ClienteRequest request = new ClienteRequest(
                "João",
                "joao@email.com",
                "41999999999"
        );

        when(repository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(
                ConflitoException.class,
                () -> service.criar(request)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void deveBuscarClientePorIdComSucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João");
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("41999999999");
        cliente.setAtivo(true);

        when(repository.findByIdAndAtivoTrue(1L))
                .thenReturn(Optional.of(cliente));

        ClienteResponse response = service.buscarPorId(1L);

        assertEquals(1L, response.id());
        assertEquals("João", response.nome());
    }

    @Test
    void deveLancarErroQuandoClienteNaoExiste() {
        when(repository.findByIdAndAtivoTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(1L)
        );
    }

    @Test
    void deveDesativarClienteComSucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setAtivo(true);

        when(repository.findByIdAndAtivoTrue(1L))
                .thenReturn(Optional.of(cliente));

        service.desativar(1L);

        assertFalse(cliente.getAtivo());

        verify(repository).save(cliente);
    }
}