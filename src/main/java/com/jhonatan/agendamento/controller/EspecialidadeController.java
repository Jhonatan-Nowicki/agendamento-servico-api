package com.jhonatan.agendamento.controller;

import com.jhonatan.agendamento.dto.request.EspecialidadeRequest;
import com.jhonatan.agendamento.dto.response.EspecialidadeResponse;
import com.jhonatan.agendamento.service.EspecialidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@Tag(name = "Especialidades", description = "Endpoints para gerenciamento de especialidades")
public class EspecialidadeController {

    private final EspecialidadeService service;

    public EspecialidadeController(EspecialidadeService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar especialidade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Especialidade criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Especialidade já cadastrada")
    })
    public ResponseEntity<EspecialidadeResponse> criar(
            @Valid @RequestBody EspecialidadeRequest request) {
        return ResponseEntity.ok(service.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar especialidades ativas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Especialidades listadas com sucesso")
    })
    public ResponseEntity<List<EspecialidadeResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar especialidade por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Especialidade encontrada"),
            @ApiResponse(responseCode = "404", description = "Especialidade não encontrada")
    })
    public ResponseEntity<EspecialidadeResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar especialidade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Especialidade atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Especialidade não encontrada"),
            @ApiResponse(responseCode = "409", description = "Especialidade já cadastrada")
    })
    public ResponseEntity<EspecialidadeResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EspecialidadeRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar especialidade")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Especialidade desativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Especialidade não encontrada")
    })
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }
}