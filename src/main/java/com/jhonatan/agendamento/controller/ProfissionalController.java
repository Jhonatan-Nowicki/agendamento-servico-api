package com.jhonatan.agendamento.controller;

import com.jhonatan.agendamento.dto.request.ProfissionalRequest;
import com.jhonatan.agendamento.dto.response.ProfissionalResponse;
import com.jhonatan.agendamento.model.Agendamento;
import com.jhonatan.agendamento.service.ProfissionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profissional criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Especialidade não encontrada ou inativa"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    public ResponseEntity<ProfissionalResponse> criar(
            @Valid @RequestBody ProfissionalRequest request) {
        return ResponseEntity.ok(service.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar profissionais ativos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profissionais listados com sucesso")
    })
    public ResponseEntity<List<ProfissionalResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar profissional por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profissional encontrado"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    public ResponseEntity<ProfissionalResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar profissional")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profissional atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Profissional ou especialidade não encontrada"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    public ResponseEntity<ProfissionalResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProfissionalRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar profissional")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Profissional desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
            @ApiResponse(responseCode = "422", description = "Profissional possui agendamentos futuros")
    })
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/disponibilidade")
    @Operation(summary = "Consultar horários ocupados do profissional por data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horários ocupados listados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    public ResponseEntity<List<Agendamento>> consultarDisponibilidade(
            @PathVariable Long id,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data) {

        return ResponseEntity.ok(service.consultarDisponibilidade(id, data));
    }
}