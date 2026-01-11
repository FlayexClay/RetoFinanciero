package com.financial.productos.controller;

import com.financial.productos.domain.dto.ProductoFinancieroDTO;
import com.financial.productos.service.ProductoFinancieroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Productos Financieros", description = "API de gestión de productos financieros")
public class ProductoFinancieroController {

    private final ProductoFinancieroService productoService;

    @GetMapping(value = "/cliente/{codigoCliente}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Obtener productos de un cliente",
            description = "Retorna todos los productos financieros de un cliente"
    )
    @ApiResponse(responseCode = "200", description = "Productos encontrados")
    public Flux<ProductoFinancieroDTO> obtenerProductosPorCliente(
            @Parameter(description = "Código único del cliente")
            @PathVariable String codigoCliente,
            @Parameter(description = "ID de seguimiento de la petición")
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {

        String trace = traceId != null ? traceId : UUID.randomUUID().toString();
        log.info("[TraceId: {}] Petición recibida para código cliente: {}",
                trace, codigoCliente);

        return productoService.obtenerProductosPorCodigoCliente(codigoCliente, trace);
    }
}