package com.jhonatan.agendamento.controller;

import com.jhonatan.agendamento.dto.request.AgendamentoRequest;
import com.jhonatan.agendamento.dto.response.AgendamentoResponse;
import com.jhonatan.agendamento.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@Tag(name = "Agendamentos", description = "Endpoints para gerenciamento de agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar agendamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente, profissional ou serviço não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito de horário")
    })
    public ResponseEntity<AgendamentoResponse> criar(@Valid @RequestBody AgendamentoRequest request) {
        return ResponseEntity.ok(service.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar agendamentos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamentos listados com sucesso")
    })
    public ResponseEntity<List<AgendamentoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    public ResponseEntity<AgendamentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/cliente/{id}")
    @Operation(summary = "Listar agendamentos por cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamentos listados com sucesso")
    })
    public ResponseEntity<List<AgendamentoResponse>> listarPorCliente(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarPorCliente(id));
    }

    @GetMapping("/profissional/{id}")
    @Operation(summary = "Listar agendamentos por profissional")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamentos listados com sucesso")
    })
    public ResponseEntity<List<AgendamentoResponse>> listarPorProfissional(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarPorProfissional(id));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar agendamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "409", description = "Regra de negócio violada")
    })
    public ResponseEntity<AgendamentoResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    @PatchMapping("/{id}/concluir")
    @Operation(summary = "Concluir agendamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento concluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "409", description = "Regra de negócio violada")
    })
    public ResponseEntity<AgendamentoResponse> concluir(@PathVariable Long id) {
        return ResponseEntity.ok(service.concluir(id));
    }
}