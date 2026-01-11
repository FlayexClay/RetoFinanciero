package com.financial.bff.controller;

import com.financial.bff.dto.ClienteInfoResponse;
import com.financial.bff.service.EncryptionService;
import com.financial.bff.service.OrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Cliente Info", description = "API para obtener información completa del cliente")
public class ClienteInfoController {

    private final OrchestrationService orchestrationService;
    private final EncryptionService encryptionService;

    @GetMapping(value = "/info/{codigoUnicoEncriptado}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Obtener información completa del cliente",
            description = "Retorna la información del cliente y todos sus productos financieros. " +
                    "El código único debe estar encriptado en Base64.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponse(responseCode = "200", description = "Información obtenida exitosamente")
    @ApiResponse(responseCode = "400", description = "Código único inválido o no puede ser desencriptado")
    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido o faltante")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public Mono<ClienteInfoResponse> obtenerInformacionCliente(
            @Parameter(description = "Código único del cliente encriptado en Base64",
                    example = "Q0xJMDAx")
            @PathVariable String codigoUnicoEncriptado,

            @Parameter(description = "ID de seguimiento distribuido (se genera automáticamente si no se provee)")
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {

        // Generar o usar traceId proporcionado
        String trace = traceId != null && !traceId.isEmpty()
                ? traceId
                : UUID.randomUUID().toString();

        log.info("[TraceId: {}] Solicitud recibida para código encriptado: {}",
                trace, codigoUnicoEncriptado);

        // Desencriptar código único
        return Mono.fromCallable(() ->
                        encryptionService.decrypt(codigoUnicoEncriptado, trace)
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Código único inválido o no puede ser desencriptado")))
                .flatMap(codigoDesencriptado -> {
                    log.info("[TraceId: {}] Código desencriptado: {}", trace, codigoDesencriptado);
                    return orchestrationService.obtenerInformacionCompleta(codigoDesencriptado, trace);
                })
                .doOnSuccess(response ->
                        log.info("[TraceId: {}] Respuesta enviada exitosamente", trace))
                .doOnError(error ->
                        log.error("[TraceId: {}] Error al procesar solicitud: {}",
                                trace, error.getMessage()));
    }
}
