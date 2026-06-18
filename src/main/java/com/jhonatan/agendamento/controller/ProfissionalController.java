package com.jhonatan.agendamento.controller;

import com.jhonatan.agendamento.dto.request.ProfissionalRequest;
import com.jhonatan.agendamento.dto.response.ProfissionalResponse;
import com.jhonatan.agendamento.service.ProfissionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.jhonatan.agendamento.model.Agendamento;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/profissionais")
@Tag(name = "Profissionais", description = "Endpoints para gerenciamento de profissionais")
public class ProfissionalController {

    private final ProfissionalService service;

    public ProfissionalController(ProfissionalService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar profissional")
    public ResponseEntity<ProfissionalResponse> criar(
            @Valid @RequestBody ProfissionalRequest request) {
        return ResponseEntity.ok(service.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar profissionais ativos")
    public ResponseEntity<List<ProfissionalResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar profissional por ID")
    public ResponseEntity<ProfissionalResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar profissional")
    public ResponseEntity<ProfissionalResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProfissionalRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar profissional")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/disponibilidade")
    @Operation(summary = "Consultar horários ocupados do profissional por data")
    public ResponseEntity<List<Agendamento>> consultarDisponibilidade(
            @PathVariable Long id,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data) {

        return ResponseEntity.ok(service.consultarDisponibilidade(id, data));
    }
}