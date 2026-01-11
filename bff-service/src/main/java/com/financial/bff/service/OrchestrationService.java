package com.financial.bff.service;

import com.financial.bff.cient.ClienteServiceClient;
import com.financial.bff.cient.ProductosServiceClient;
import com.financial.bff.dto.ClienteDTO;
import com.financial.bff.dto.ClienteInfoResponse;
import com.financial.bff.dto.ProductoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrchestrationService {

    private final ClienteServiceClient clienteServiceClient;
    private final ProductosServiceClient productosServiceClient;

    // BiFunction para combinar cliente y productos
    private final BiFunction<ClienteDTO, List<ProductoDTO>, ClienteInfoResponse>
            combineClienteAndProductos = (cliente, productos) ->
            ClienteInfoResponse.builder()
                    .nombres(cliente.getNombres())
                    .apellidos(cliente.getApellidos())
                    .tipoDocumento(cliente.getTipoDocumento())
                    .numeroDocumento(cliente.getNumeroDocumento())
                    .productos(productos)
                    .build();

    public Mono<ClienteInfoResponse> obtenerInformacionCompleta(
            String codigoUnico, String traceId) {

        log.info("[TraceId: {}] Iniciando orquestación para código: {}",
                traceId, codigoUnico);

        // Llamada paralela a ambos servicios
        Mono<ClienteDTO> clienteMono = clienteServiceClient
                .obtenerCliente(codigoUnico, traceId)
                .cache();

        Mono<List<ProductoDTO>> productosMono = productosServiceClient
                .obtenerProductos(codigoUnico, traceId)
                .collectList()
                .cache();

        // Combinar resultados usando zip y BiFunction
        return Mono.zip(clienteMono, productosMono)
                .map(tuple -> {
                    ClienteInfoResponse response = combineClienteAndProductos
                            .apply(tuple.getT1(), tuple.getT2());
                    response.setTraceId(traceId);

                    log.info("[TraceId: {}] Orquestación completada exitosamente", traceId);
                    return response;
                })
                .doOnError(error ->
                        log.error("[TraceId: {}] Error en orquestación: {}",
                                traceId, error.getMessage()));
    }
}