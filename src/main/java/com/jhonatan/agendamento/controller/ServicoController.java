package com.jhonatan.agendamento.controller;

import com.jhonatan.agendamento.dto.request.ServicoRequest;
import com.jhonatan.agendamento.dto.response.ServicoResponse;
import com.jhonatan.agendamento.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @PostMapping
    public ServicoResponse criar(@Valid @RequestBody ServicoRequest request) {
        return servicoService.criar(request);
    }

    @GetMapping
    public List<ServicoResponse> listar() {
        return servicoService.listar();
    }

    @GetMapping("/{id}")
    public ServicoResponse buscarPorId(@PathVariable Long id) {
        return servicoService.buscarPorId(id);
    }

    @GetMapping("/especialidade/{especialidadeId}")
    public List<ServicoResponse> listarPorEspecialidade(@PathVariable Long especialidadeId) {
        return servicoService.listarPorEspecialidade(especialidadeId);
    }

    @PutMapping("/{id}")
    public ServicoResponse atualizar(@PathVariable Long id,
                                     @Valid @RequestBody ServicoRequest request) {
        return servicoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void desativar(@PathVariable Long id) {
        servicoService.desativar(id);
    }
}