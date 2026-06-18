package com.jhonatan.agendamento.controller;

import com.jhonatan.agendamento.dto.request.ServicoRequest;
import com.jhonatan.agendamento.dto.response.ServicoResponse;
import com.jhonatan.agendamento.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
@Tag(name = "Serviços", description = "Endpoints para gerenciamento de serviços")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @PostMapping
    @Operation(summary = "Criar serviço")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Especialidade não encontrada")
    })
    public ServicoResponse criar(@Valid @RequestBody ServicoRequest request) {
        return servicoService.criar(request);
    }

    @GetMapping
    @Operation(summary = "Listar serviços ativos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviços listados com sucesso")
    })
    public List<ServicoResponse> listar() {
        return servicoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviço encontrado"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ServicoResponse buscarPorId(@PathVariable Long id) {
        return servicoService.buscarPorId(id);
    }

    @GetMapping("/especialidade/{especialidadeId}")
    @Operation(summary = "Listar serviços por especialidade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviços listados com sucesso")
    })
    public List<ServicoResponse> listarPorEspecialidade(@PathVariable Long especialidadeId) {
        return servicoService.listarPorEspecialidade(especialidadeId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Serviço ou especialidade não encontrada")
    })
    public ServicoResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ServicoRequest request) {

        return servicoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar serviço")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Serviço desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public void desativar(@PathVariable Long id) {
        servicoService.desativar(id);
    }
}