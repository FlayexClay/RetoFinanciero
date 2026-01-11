package com.financial.cliente.controller;

import com.financial.cliente.domain.dto.ClienteDTO;
import com.financial.cliente.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cliente", description = "API de gestion de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping(value = "/{codigoUnico}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Obtener cliente por codigo unico",
            description = "Retorna la informacion del cliente basado en su codigo unico"
    )
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public Mono<ClienteDTO> obtenerCliente(
            @Parameter(description = "Codigo unico del cliente")
            @PathVariable String codigoUnico,
            @Parameter(description = "ID de seguimiento de la peticion")
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId){

        String trace = traceId != null ? traceId : UUID.randomUUID().toString();
        log.info("[TraceId: {}] Peticion recibida para codigo unico: {}", trace, codigoUnico);

        return clienteService.obtenerClientePorCodigoUnico(codigoUnico, trace);
    }

}
